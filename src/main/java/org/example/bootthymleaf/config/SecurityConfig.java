package org.example.bootthymleaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/word")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/update")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/delete")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/reset")).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
