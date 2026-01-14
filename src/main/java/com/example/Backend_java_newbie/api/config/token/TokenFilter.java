package com.example.Backend_java_newbie.api.config.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Backend_java_newbie.application.service.TokenBlacklistService;
import com.example.Backend_java_newbie.domain.interfaces.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "accessToken";
    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;

    public TokenFilter(TokenService tokenService, TokenBlacklistService blacklistService) {
        this.tokenService = tokenService;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractBearerToken(request);
        if (ObjectUtils.isEmpty(token)) {
            token = extractTokenFromCookie(request);
        }

        if (ObjectUtils.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT decoded = tokenService.verify(token);
        if (decoded == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jti = decoded.getId();
        if (!ObjectUtils.isEmpty(jti) && blacklistService.isBlacklisted(jti)) {
            filterChain.doFilter(request, response);
            return;
        }

        String principal = decoded.getClaim("principal").asString();
        String role = decoded.getClaim("role").asString();

        List<SimpleGrantedAuthority> authorities = List.of();
        if (!ObjectUtils.isEmpty(role)) {
            authorities = List.of(new SimpleGrantedAuthority(role));
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(authorization)) return null;
        if (!authorization.startsWith("Bearer ")) return null;
        return authorization.substring(7).trim();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                String value = c.getValue();
                return ObjectUtils.isEmpty(value) ? null : value.trim();
            }
        }
        return null;
    }
}

