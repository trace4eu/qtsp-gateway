package com.trace4eu.tsaClient.security;

import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = extractBearerToken(request);
        if (bearerToken != null) {
            TokenIntrospectionResponse tokenIntrospectionResponse = validateToken(bearerToken);
            if (tokenIntrospectionResponse.isActive()) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(tokenIntrospectionResponse.getSub(), null, Collections.emptyList()));
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

    private TokenIntrospectionResponse validateToken(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(adminBearerToken);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("token", bearerToken);
        map.add("scope", requiredScope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity(introspectionEndpoint, request, TokenIntrospectionResponse.class).getBody();
    }
}

class TokenIntrospectionResponse {
    private boolean active;
    @Nullable
    private String scope;
    @Nullable
    private String client_id;
    @Nullable
    private String sub;
    @Nullable
    private String exp;
    @Nullable
    private String iat;
    @Nullable
    private String nbf;
    @Nullable
    private String[] aud;
    @Nullable
    private String iss;
    @Nullable
    private String token_type;
    @Nullable
    private String token_use;

    @Nullable
    public String getScope() {
        return scope;
    }

    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Nullable
    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(@Nullable String client_id) {
        this.client_id = client_id;
    }

    @Nullable
    public String getSub() {
        return sub;
    }

    public void setSub(@Nullable String sub) {
        this.sub = sub;
    }

    @Nullable
    public String getExp() {
        return exp;
    }

    public void setExp(@Nullable String exp) {
        this.exp = exp;
    }

    @Nullable
    public String getIat() {
        return iat;
    }

    public void setIat(@Nullable String iat) {
        this.iat = iat;
    }

    @Nullable
    public String getNbf() {
        return nbf;
    }

    public void setNbf(@Nullable String nbf) {
        this.nbf = nbf;
    }

    @Nullable
    public String[] getAud() {
        return aud;
    }

    public void setAud(@Nullable String[] aud) {
        this.aud = aud;
    }

    @Nullable
    public String getIss() {
        return iss;
    }

    public void setIss(@Nullable String iss) {
        this.iss = iss;
    }

    @Nullable
    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(@Nullable String token_type) {
        this.token_type = token_type;
    }

    @Nullable
    public String getToken_use() {
        return token_use;
    }

    public void setToken_use(@Nullable String token_use) {
        this.token_use = token_use;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



}