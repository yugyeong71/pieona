package com.example.pieona.service;

import com.example.pieona.common.SuccessMessage;
import com.example.pieona.dto.ListUser;
import com.example.pieona.dto.SignRequest;
import com.example.pieona.dto.SignResponse;
import com.example.pieona.dto.TokenDto;
import com.example.pieona.entity.Authority;
import com.example.pieona.entity.User;
import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.jwt.Token;
import com.example.pieona.repo.TokenRepository;
import com.example.pieona.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final TokenRepository tokenRepository;

    public SignResponse login(SignRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 정보입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("잘못된 정보입니다.");
        }

        user.setRefreshToken(createRefreshToken(user));

        return SignResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .gender(user.getGender())
                .token(TokenDto.builder()
                        .access_token(jwtProvider.createToken(user.getEmail(), user.getRoles()))
                        .refresh_token(user.getRefreshToken())
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
                    .build();

            user.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            userRepository.save(user);

        } catch (Exception e){
            throw new Exception("잘못된 요청입니다.");
        }

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


    /*
        Refresh Token
        Redis 내부에는
        refreshToken:memberId : tokenValue
        형태로 저장한다.
     */

    public String createRefreshToken(User user){
        Token token = tokenRepository.save(
                Token.builder()
                        .id(user.getId())
                        .refresh_token(UUID.randomUUID().toString())
                        .expiration(300)
                        .build()
        );

        user.setRefreshToken(token.getRefresh_token());

        return token.getRefresh_token();
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

    public TokenDto refreshAccessToken(TokenDto token) throws Exception{

        String email = jwtProvider.getEmail(token.getAccess_token());
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정 정보입니다."));
        Token refreshToken = vaildRefreshToken(user, token.getRefresh_token());

        if(refreshToken != null){
            return TokenDto.builder()
                    .access_token(jwtProvider.createToken(email, user.getRoles()))
                    .refresh_token(refreshToken.getRefresh_token())
                    .build();
        }else {
            throw new Exception("로그인을 해주세요.");
        }
    }


}
