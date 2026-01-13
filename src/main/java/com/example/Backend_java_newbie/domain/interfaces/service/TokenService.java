package com.example.Backend_java_newbie.domain.interfaces.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Backend_java_newbie.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.issuer}")
    private String issuer;

    @Value("${app.token.access-token-ttl-seconds:3600}")
    private long accessTokenTtlSeconds;

    public String tokenize(User user) {
        Algorithm algorithm = algorithm();

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenTtlSeconds);

        return JWT.create()
                .withIssuer(issuer)
                .withClaim("principal", user.getId())
                .withClaim("role", "ROLE_USER")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(algorithm);
    }

    public long getExpiresInSeconds(DecodedJWT decodedJWT) {
        if (decodedJWT == null || decodedJWT.getExpiresAt() == null) return 0;
        long diff = decodedJWT.getExpiresAt().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(diff, 0);
    }

    public DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm())
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            // หมดอายุ → ถือว่าไม่ผ่าน
            return null;
        } catch (JWTVerificationException e) {
            // token ไม่ถูกต้อง
            return null;
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }
}
