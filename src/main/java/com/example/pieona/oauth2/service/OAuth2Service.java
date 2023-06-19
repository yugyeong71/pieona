package com.example.pieona.oauth2.service;

import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.jwt.dto.TokenResponseDto;
import com.example.pieona.oauth2.CustomOAuth2User;
import com.example.pieona.user.Role;
import com.example.pieona.user.entity.User;
import com.example.pieona.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TokenResponseDto> oauthLogin(CustomOAuth2User oAuth2User){

        String userEmail = oAuth2User.getEmail();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BadCredentialsException("일치하는 회원 정보가 존재하지 않습니다."));

        String accessToken = jwtProvider.createAccessToken(userEmail, Role.USER);
        String refreshToken = jwtProvider.createRefreshToken(user);

        user.setRefreshToken(refreshToken);

        TokenResponseDto tokenDto = new TokenResponseDto(user.getId(), accessToken, refreshToken, jwtProvider.getAccessExp(), jwtProvider.getRefreshExp());

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);

    }

}
