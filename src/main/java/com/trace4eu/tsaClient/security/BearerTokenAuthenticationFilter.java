package com.trace4eu.tsaClient.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
    private final String introspectionEndpoint;
    private final String adminBearerToken;
    private final String requiredScope;
    private final RestTemplate restTemplate;

    public BearerTokenAuthenticationFilter(String introspectionEndpoint, String adminBearerToken, String requiredScope) {
        this.introspectionEndpoint = introspectionEndpoint;
        this.adminBearerToken = adminBearerToken;
        this.requiredScope = requiredScope;
        this.restTemplate = new RestTemplate();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = extractBearerToken(request);
        if (bearerToken != null) {
            boolean isValid = validateToken(bearerToken);
            if (isValid) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList()));
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(adminBearerToken);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("token", bearerToken);
        map.add("scope", requiredScope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(introspectionEndpoint, request, Map.class);
            Map responseHydra = response.getBody();
            return Boolean.TRUE.equals(response.getBody().get("active"));
        } catch (Exception e) {
            return false;
        }
    }
}