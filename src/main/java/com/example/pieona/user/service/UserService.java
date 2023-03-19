package com.example.pieona.user.service;

import com.example.pieona.common.SecurityUtil;
import com.example.pieona.common.SuccessMessage;
import com.example.pieona.user.Role;
import com.example.pieona.user.dto.*;
import com.example.pieona.jwt.dto.TokenDto;
import com.example.pieona.user.entity.Authority;
import com.example.pieona.user.entity.User;
import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.jwt.Token;
import com.example.pieona.jwt.repo.TokenRepository;
import com.example.pieona.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
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

    private final JavaMailSender javaMailSender;


    public SignResponse login(SignRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 정보입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("잘못된 정보입니다.");
        }


        String refreshToken = jwtProvider.createRefreshToken(user);

        user.setRefreshToken(refreshToken);

        Token token = new Token(user.getId(), refreshToken, jwtProvider.getRefreshExp());

        tokenRepository.save(token);

//        redisTemplate.opsForValue().set(refreshToken, user.getEmail());


        return SignResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .gender(user.getGender())
                .token(TokenDto.builder()
                        .access_token(jwtProvider.createAccessToken(user.getEmail(), Role.USER))
                        .refresh_token(refreshToken)
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

    public SuccessMessage oauthUpdateUser(UpdateUserDto updateUserDto){
        User user = userRepository
                .findByEmail(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

        user.oauth2Update(updateUserDto);

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
                token.setExpiration(1000L);
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
                    .access_token(jwtProvider.createAccessToken(email, Role.USER))
                    .refresh_token(jwtProvider.createRefreshToken(user))
                    .build();
        } else {
            throw new Exception("로그인을 해주세요.");
        }
    }

    public SuccessMessage updatePassword(String asIsPassword, String newPassword, String email) throws Exception {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

        if(!user.matchPassword(passwordEncoder, asIsPassword) ) {
            throw new Exception();
        }

        user.updatePassword(passwordEncoder, newPassword);

        return new SuccessMessage();
    }

    public MailDto mailTemporary(String email) throws Exception{

        userRepository.findByEmail(email).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

        String temPassword = getTempPassword(); // 임시 비밀번호 생성
        MailDto mailDto = new MailDto(); // 메일 작성
        mailDto.setAddress(email);
        mailDto.setTitle("피어나 임시 비밀번호 발급");
        mailDto.setMessage("안녕하세요. 피어나 임시 비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
                + temPassword + " 입니다." + "로그인 후에 비밀번호를 변경을 해주세요");

        updateTemPassword(temPassword, email); // 임시 비밀번호로 DB 업데이트

        return mailDto;
    }

    // 임시 비밀번호 생성
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        int idx;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    // 임시 비밀번호로 DB 업데이트
    public void updateTemPassword(String temPassword, String email) throws Exception{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("회원 정보가 존재하지 않습니다."));

        String password = temPassword;

        user.updatePassword(passwordEncoder, password);
    }

    public void mailSend(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());

        javaMailSender.send(message);
    }

}
