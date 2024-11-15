package com.trace4eu.tsaClient.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${trace4eu.security.introspect-endpoint}")
    private String introspectionEndpoint;

    @Value("${trace4eu.security.admin-token}")
    private String adminBearerToken;

    @Value("${trace4eu.security.required-scope}")
    private String requiredScope;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/timestamp", "/verify").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new BearerTokenAuthenticationFilter(
                                introspectionEndpoint, adminBearerToken, requiredScope),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}


