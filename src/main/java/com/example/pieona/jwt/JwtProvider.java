package com.example.pieona.jwt;

import com.example.pieona.user.Role;
import com.example.pieona.security.JpaUserDetailsService;
import com.example.pieona.user.entity.User;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/*
    Token 발급해주는 클래스
 */
@Getter @Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String salt;

    private Key secretKey;

    private final long accessExp = 1000L * 60 * 30; // 액세스 토큰 만료 시간 : 30분

    private final long refreshExp = 7 * 24 * 60 * 60 * 1000L; // 리프레시 토큰 만료 시간 : 7시간

    private final JpaUserDetailsService userDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }


    /*
        Access Token 발급
     */

    public String createAccessToken(String email, Role role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + accessExp)) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 사용할 알고리즘과 signature 에 들어갈 secretKey
                .compact();
    }

    /*
        Refresh Token 발급
     */
    public String createRefreshToken(User user){

        Date now = new Date();

        Token token = Token.builder()
                .id(user.getId())
                .refresh_token(Jwts.builder()
                        .setIssuedAt(now) // 토큰 발행 시간
                        .setExpiration(new Date(now.getTime() + refreshExp)) // 만료 시간
                        .signWith(secretKey, SignatureAlgorithm.HS256) // 사용할 알고리즘과 signature 에 들어갈 secretKey
                        .compact())
                .build();

        return token.getRefresh_token();

    }

    /*
        권한 정보 획득
        Spring Security 인증 과정에서 권한 확인을 위한 기능
     */
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /*
        토큰에 담겨있는 유저 email 획득
     */
    public String getEmail(String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    /*
        Authorization Header 를 통해 인증
     */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    /*
        토큰 검증
     */
    public boolean validateToken(String token){
        try{
            // Bearer 검증
            if(!token.substring(0, "Bearer ".length()).equalsIgnoreCase("Bearer ")){
                return false;
            }

            else {
                token = token.split(" ")[1].trim();
            }

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());

        } catch (Exception e){
            return false;
        }
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody().getExpiration();

        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

}