package com.demo.shopapp.shared.components;

import com.demo.shopapp.domain.user.entity.Token;
import com.demo.shopapp.domain.user.entity.User;
import com.demo.shopapp.domain.user.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    // số giây
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user ) throws InvalidParameterException {
        if (user.getPhoneNumber() == null && user.getEmail() == null) {
            logger.error("Cannot generate token: Both phoneNumber and email are null for userId: {}", user.getId());
            throw new InvalidParameterException("User must have either phone number or email");
        }

        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());

        // Lưu danh sách roleNames vào token
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .toList();
        claims.put("roles", roleNames);

        try {
            String subject = user.getEmail() != null ? user.getEmail() : user.getPhoneNumber();

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject( user.getEmail() == null ? user.getPhoneNumber() : user.getEmail()) // ưu tiên email
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Generated token for userId: {} with subject: {}", user.getId(), subject);
            return token;

        }catch (Exception e) {
            logger.error("Failed to generate token for userId: {} due to: {}", user.getId(), e.getMessage(), e);
            throw new InvalidParameterException("Cannot create jwt token, error: " + e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    // có claim rồi sao extract ra ?
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage(), e);
            throw new InvalidParameterException("Invalid token format: " + e.getMessage());
        }
    }

    // functional interface
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Long  extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        // expirationDate mà bé hơn ngày hiện tại => hết hạn rồi
        return expirationDate.before(new Date());
    }

    public String extractIdentifier(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get("email", String.class);
        String phoneNumber = claims.get("phoneNumber", String.class);

        if (email != null) return email;
        if (phoneNumber != null) return phoneNumber;
        logger.error("No valid identifier (email or phone) found in token");
        throw new InvalidParameterException("Token must contain either email or phone number");
    }


    // check thu hồi
    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::getRevoked)
                .orElse(true); // Nếu không tìm thấy token, coi như bị thu hồi
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        if (!validateTokenFormat(token)) {
            logger.warn("Invalid token format: {}", token);
            return false;
        }

        String tokenIdentifier = extractIdentifier(token);
        String userDetailsUsername = userDetails.getUsername();

        if (tokenIdentifier == null || userDetailsUsername == null) {
            logger.error("Token identifier or username is null");
            return false;
        }

        boolean isValid = tokenIdentifier.equals(userDetailsUsername) &&
                !isTokenExpired(token) &&
                !isTokenRevoked(token);
        if (!isValid) {
            logger.warn("Token validation failed for token: {}, username: {}", token, userDetailsUsername);
        } else {
            logger.info("Token validated successfully for username: {}", userDetailsUsername);
        }
        return isValid;
    }

    public boolean validateTokenFormat(String token) {
        return token != null && !token.isBlank();
    }

}