package com.example.pieona.user.service;

import com.example.pieona.common.SecurityUtil;
import com.example.pieona.common.SuccessMessage;
import com.example.pieona.user.Role;
import com.example.pieona.user.dto.ListUser;
import com.example.pieona.user.dto.SignRequest;
import com.example.pieona.user.dto.SignResponse;
import com.example.pieona.jwt.dto.TokenDto;
import com.example.pieona.user.dto.UpdateUserDto;
import com.example.pieona.user.entity.Authority;
import com.example.pieona.user.entity.User;
import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.jwt.Token;
import com.example.pieona.jwt.repo.TokenRepository;
import com.example.pieona.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final TokenRepository tokenRepository;

    private final RedisTemplate redisTemplate;


    public SignResponse login(SignRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 정보입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("잘못된 정보입니다.");
        }

        user.setRefreshToken(jwtProvider.createRefreshToken(user));

        return SignResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .gender(user.getGender())
                .token(TokenDto.builder()
                        .access_token(jwtProvider.createAccessToken(user.getEmail(), Role.USER))
                        .refresh_token(jwtProvider.createRefreshToken(user))
                        //.refresh_token(user.getRefreshToken())
                        .build())
                .build();
    }

    public SuccessMessage signUp(SignRequest request) throws Exception{
        try{
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .image(request.getImage())
                    .gender(request.getGender())
                    .role(Role.USER)
                    .build();

            user.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            userRepository.save(user);

        } catch (Exception e){
            throw new Exception("잘못된 요청입니다.");
        }

        return new SuccessMessage();
    }


    public SuccessMessage logout(String token, HttpServletRequest request){

        token = jwtProvider.resolveToken(request);

        if (!jwtProvider.validateToken(token)){
            throw new IllegalArgumentException("로그아웃 : 유효하지 않은 토큰입니다.");
        }

        token = token.split(" ")[1].trim();
        Authentication authentication = jwtProvider.getAuthentication(token);

        if (redisTemplate.opsForValue().get("RT:" + authentication.getName())!=null){
            // Refresh Token을 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        Long expiration = jwtProvider.getExpiration(token);
        redisTemplate.opsForValue().set(token,"logout",expiration,TimeUnit.MILLISECONDS);

        return new SuccessMessage();
    }


    public boolean existEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean existNickname(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public ListUser listUser(Long id){

        User user = userRepository.findById(id).orElseThrow(() -> new BadCredentialsException("일치하는 회원 정보가 존재하지 않습니다."));

        return ListUser.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .gender(user.getGender())
                .image(user.getImage())
                .build();
    }

    public SuccessMessage updateUser(UpdateUserDto updateUserDto){
        User user = userRepository
                .findByEmail(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

        user.update(updateUserDto);

        return new SuccessMessage();
    }


    public SuccessMessage delUser(){
        String userId = SecurityUtil.getLoginUsername();

        if (userId == null){
            throw new RuntimeException("로그인 유저 정보가 없습니다.");
        }

        userRepository.deleteByEmail(userId);

        return new SuccessMessage();
    }


    public Token vaildRefreshToken(User user, String refreshToken) throws Exception{
        Token token = tokenRepository.findById(user.getId()).orElseThrow(() -> new Exception("만료된 계정입니다. 로그인을 다시 시도하세요."));

        // 해당 유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
        if(token.getRefresh_token() == null){
            return null;
        } else {
            if(token.getExpiration() < 10){ // RefreshToken 만료일자가 얼마 남지 않았을 때 만료시간 연장
                token.setExpiration(1000);
                tokenRepository.save(token);
            }

            if (!token.getRefresh_token().equals(refreshToken)){ // 토큰이 같은지 비교
                return null;
            } else {
                return token;
            }
        }
    }


    public SuccessMessage updatePassword(String asIsPassword, String toBePassword, String email) throws Exception {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

        if(!user.matchPassword(passwordEncoder, asIsPassword) ) {
            throw new Exception();
        }

        user.updatePassword(passwordEncoder, toBePassword);

        return new SuccessMessage();
    }

    public TokenDto refreshAccessToken(TokenDto token) throws Exception{

        String email = jwtProvider.getEmail(token.getAccess_token());

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정 정보입니다."));

        Token refreshToken = vaildRefreshToken(user, token.getRefresh_token());

        if(refreshToken != null){
            return TokenDto.builder()
                    .access_token(jwtProvider.createAccessToken(email, Role.USER))
                    .refresh_token(jwtProvider.createRefreshToken(user))
                    .build();
        } else {
            throw new Exception("로그인을 해주세요.");
        }
    }



}
