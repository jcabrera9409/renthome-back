package com.surrender.service.impl;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.surrender.model.Usuario;
import com.surrender.repo.TokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token-expiration}")
    private long accessTokenExpire;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpire;

    @Autowired
    private TokenRepo tokenRepo;

    public String generateAccessToken(Usuario usuario) {
        return generateToken(usuario, accessTokenExpire);
    }

    private String generateToken(Usuario usuario, long expireTime) {
        String token = Jwts
                .builder()
                .subject(usuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime ))
                .claim("name", usuario.getNombre())
                .signWith(getSigninKey())
                .compact();

        return token;
    }

    public String generateRefreshToken(Usuario usuario) {
        return generateToken(usuario, refreshTokenExpire );
    }
    
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        boolean isValidToken = tokenRepo
            .findByAccessToken(token)
            .map(t -> !t.isLoggedOut())
            .orElse(false);
        
        return username.equals(user.getUsername()) && !isTokenExpired(token) && isValidToken;
    }
    
    public boolean isValidRefreshToken(String token, Usuario usuario) {
        String username = extractUsername(token);

        boolean validRefreshToken = tokenRepo
                .findByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return username.equals(usuario.getUsername()) && !isTokenExpired(token) && validRefreshToken;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
