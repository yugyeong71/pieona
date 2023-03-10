package com.example.pieona.config;

import com.example.pieona.jwt.JwtAuthenticationFilter;
import com.example.pieona.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

    private static final String[] ALL_PERMIT = {
            "/signup/**",
            "/login",
            "/social",
            "/refresh",
            "/auth/**",
            "/oauth2/**",
            "/",
            "/member/**",
            "/list"
    };

    private static final String[] ADMIN_PERMIT = {
            "/admin/**"
    };

    private static final String[] USER_PERMIT = {
            "/user/**",
            "/post/**"
    };

    private final JwtProvider jwtProvider;



    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic().disable() // ID, Password ???????????? Base64??? ??????????????? ???????????? ??????
                .formLogin().disable() // ????????? ??? ?????????
                .csrf().disable() // ?????? ????????? ?????? JWT ??????????????? ???????????? ??????
                .cors(
                        c -> {
                            CorsConfigurationSource source = request -> {
                                // Cors ?????? ??????
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(
                                        List.of("*")
                                );
                                config.setAllowedMethods(
                                        List.of("*")
                                );
                                return config;
                            };
                            c.configurationSource(source);
                        }
                )
                // Spring Security ?????? ?????? : ????????? ?????? ??? ???????????? ??????
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                // ???????????? ?????? ??????/?????? ??????
                .authorizeHttpRequests()
                .requestMatchers(ALL_PERMIT).permitAll()
                .requestMatchers(ADMIN_PERMIT).hasRole("ADMIN")
                .requestMatchers(USER_PERMIT).hasRole("USER")
                .anyRequest().authenticated()
                .and()
                // JWT ?????? ?????? ??????
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                // ?????? ?????????
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // ?????? ????????? ???????????? ??? ??? ????????? ????????????.
                        response.setStatus(403);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("????????? ?????? ??????????????????.");
                    }
                })
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        // ??????????????? ???????????? ??? ??? ????????? ????????????.
                        response.setStatus(401);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("????????? ???????????????.");
                    }
                });

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**"));
    }
}
