package com.dogpaws.backend.config;

import com.dogpaws.backend.filter.AdminLoginFilter;
import com.dogpaws.backend.filter.JWTFilter;
import com.dogpaws.backend.filter.LoginFilter;
import com.dogpaws.backend.utils.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.dogpaws.backend.service.ajy.TokenService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, TokenService tokenService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEnoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트 환경)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/join","/board/*","/board/boardDetail/","/store/*").permitAll() // /admin/login은 필터 제외
                        .requestMatchers("/social/**","/naver/**","/kakao/**","/google/**").permitAll() // /admin/login은 필터 제외
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/login").permitAll() // /admin/login은 필터 제외
                        .requestMatchers("/admin/**").authenticated() // /admin/* 경로는 인증 필요
                        .anyRequest().authenticated() // 모든 요청 허용 (테스트 환경)
                );

        http
                .formLogin((auth) -> auth.disable());

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new AdminLoginFilter(authenticationManager(authenticationConfiguration),jwtUtil,tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:2000");
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-refresh"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    MappingJackson2JsonView jsonView() {
        //
        return new MappingJackson2JsonView();
    }
}
