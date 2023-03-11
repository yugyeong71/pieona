package com.example.pieona.jwt;

import com.example.pieona.user.entity.Authority;
import com.example.pieona.security.JpaUserDetailsService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String salt;

    private Key secretKey;

    private final long exp = 1000L * 60 * 30; // 만료시간 : 30분

    private final JpaUserDetailsService userDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String createToken(String memId, List<Authority> roles){
        Claims claims = Jwts.claims().setSubject(memId);
        claims.put("roles", roles);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 권한 정보 획득
    // Spring Security 인증 과정에서 권한 확인을 위한 기능
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 memId 획득
    public String getEmail(String token){

        /*try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJwt(token).getBody().getSubject();
        } catch (ExpiredJwtException e){
            e.printStackTrace();
            return e.getClaims().getSubject();
        } catch (Exception e){
            e.printStackTrace();
        }
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJwt(token).getBody().getSubject();*/

        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    // 토큰 검증
    public boolean validateToken(String token){
        try{
            // Bearer 검증
            if(!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")){
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

}