package com.demo.shopapp.components;


import com.demo.shopapp.entities.User;
import com.demo.shopapp.repositorys.TokenRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("roleId", (user.getRole() == null) ? 2 : user.getRole().getId());
        claims.put("userId", user.getId());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;

        }catch (Exception e) {
            throw new InvalidParameterException("Cannot create jwt token, error: " + e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    // có claim rồi sao extract ra ?
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) //giải mã token
                .build()
                .parseClaimsJws(token) //Parse chuỗi token để lấy ra tất cả các claims
                .getBody(); //Lấy phần payload của token, nơi chứa các claims
    }

    // functional interface
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        // expirationDate mà bé hơn ngày hiện tại => hết hạn rồi
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
       if (!validateTokenFormat(token)){
           return false;
       }
        //  UserDetails trong Spring Security thường được lưu trong Security Context Holder sau khi người dùng đăng nhập thành công.
        String phoneNumber = extractPhoneNumber(token);
        // check token = giống trong context holder và chưa hết hạn
        return phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean validateTokenFormat(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return true;
    }



}