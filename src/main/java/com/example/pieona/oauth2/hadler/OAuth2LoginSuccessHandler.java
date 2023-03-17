package com.example.pieona.oauth2.hadler;
import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.oauth2.CustomOAuth2User;
import com.example.pieona.user.Role;
import com.example.pieona.user.entity.User;
import com.example.pieona.user.repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("성공");
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if(oAuth2User.getRole() == Role.GUEST) {
                jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole());
                response.sendRedirect("/signup"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }

    }
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User){
        jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole());
        jwtService.createRefreshToken(User.builder().build());

    }

}
