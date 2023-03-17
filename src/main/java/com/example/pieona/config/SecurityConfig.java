package com.example.pieona.config;

import com.example.pieona.jwt.JwtAuthenticationFilter;
import com.example.pieona.jwt.JwtProvider;
import com.example.pieona.oauth2.hadler.OAuth2LoginFailureHandler;
import com.example.pieona.oauth2.hadler.OAuth2LoginSuccessHandler;
import com.example.pieona.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RedisTemplate redisTemplate;

    private static final String[] ALL_PERMIT = {
            "/signup/**",
            "/login",
            "/social",
            "/refresh",
            "/auth/**",
            "/oauth2/**",
            "/",
            "/member/**",
            "/list",
            "/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**"
    };

    private static final String[] ADMIN_PERMIT = {
            "/admin/**"
    };

    private static final String[] USER_PERMIT = {
            "/user/**",
            "/post/**"
    };

    private final JwtProvider jwtProvider;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

            http.httpBasic().disable().formLogin().disable().csrf().disable()
                .cors(c -> {CorsConfigurationSource source = request -> {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(List.of("*"));
                                config.setAllowedMethods(List.of("*"));
                                return config;};c.configurationSource(source);})
                // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                // 조건별로 요청 허용/제한 설정
                .authorizeHttpRequests()
                .requestMatchers(ALL_PERMIT).permitAll()
                .requestMatchers(ADMIN_PERMIT).hasRole("ADMIN")
                .requestMatchers(USER_PERMIT).hasRole("USER")
                .anyRequest().authenticated()
                .and()
                // JWT 인증 필터 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                // 에러 핸들링
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // 권한 문제가 발생했을 때 이 부분을 호출한다.
                        response.setStatus(403);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("권한이 없는 사용자입니다.");
                    }})
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        // 인증문제가 발생했을 때 이 부분을 호출한다.
                        response.setStatus(401);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("인증이 필요합니다.");
                    }})
                    .and().oauth2Login().successHandler(oAuth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                    .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
                    .userInfoEndpoint().userService(customOAuth2UserService);
        return http.build();

    }
}
