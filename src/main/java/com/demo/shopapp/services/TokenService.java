package com.demo.shopapp.services;
import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.entities.Token;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.exceptions.ExpiredTokenException;
import com.demo.shopapp.repositorys.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class TokenService {

    private static final int MAX_TOKENS = 3;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable


    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;


    public boolean isMobileDevice(@NotNull String userAgent) {
        String device = userAgent.toLowerCase();
        return  device.contains("android") || device.contains("iphone") || device.contains("mobile");
    }

    public Token saveToken(User user, String newToken, String userAgent) {
        try{
            // Kiểm tra token moi nếu là mobile hay không
            boolean isMobile = isMobileDevice(userAgent);

            // tạo token mới
            Token tokenEntity = Token.builder()
                    .user(user)
                    .token(newToken)
                    .tokenType("Bearer")
                    .expirationDate(LocalDateTime.now().plusDays(7)) // Token sống 7 ngày
                    .revoked(false)
                    .expired(false)
                    .isMobile(isMobile)
                    .build();

            List<Token> tokens = this.tokenRepository.findByUserIdAndExpiredFalseAndRevokedFalse(user.getId());

            if ( tokens != null && tokens.size() >= MAX_TOKENS ) {
                boolean flag = false;
                for (Token token : tokens) {
                    // Nếu token không phải của mobile, đánh dấu revoked
                    if (!isMobile) {
                        token.setRevoked(true);
                        tokenRepository.save(token);
                        flag = true;
                        break;
                    }
                }
                // neu tat ca la mobile
                if(!flag){
                    if(!tokens.isEmpty()){
                        Token oldestToken = tokens.get(0);  // token cũ nhất là phần tử đầu tiên
                        oldestToken.setRevoked(true);  // Đánh dấu revoked cho token cũ nhất
                        tokenRepository.save(oldestToken);  // Lưu lại sự thay đổi của token cũ
                    }

                }
            }
            // Gia hạn thời gian expiration
            long expirationInSeconds = expiration;
            // thời gian hiện tại + khoảng thời gian
            LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);

            tokenEntity.setRefreshToken(UUID.randomUUID().toString());
            tokenEntity.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

            // Lưu token mới vào cơ sở dữ liệu
            return tokenRepository.save(tokenEntity);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Token refreshToken(String refreshToken, User user) throws Exception {
        Optional<Token> existingToken = tokenRepository.findByRefreshToken(refreshToken);

        if(existingToken.isEmpty()) {
            throw new DataNotFoundException("Refresh token does not exist");
        }

        if(existingToken.get().getRefreshExpirationDate().compareTo(LocalDateTime.now()) < 0){
            tokenRepository.delete(existingToken.get());
            throw new ExpiredTokenException("Refresh token is expired");
        }
        String token = this.jwtTokenUtils.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        existingToken.get().setExpirationDate(expirationDateTime);
        existingToken.get().setToken(token);
        existingToken.get().setRefreshToken(UUID.randomUUID().toString());
        existingToken.get().setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(existingToken.get());
        return existingToken.get();
    }

    public void deleteToken(String token) {
        Optional<Token> tokenEntity = tokenRepository.findByToken(token);
        if (tokenEntity.isPresent()) {
            tokenRepository.delete(tokenEntity.get());
        } else {
            throw new RuntimeException("Token not found.");
        }
    }


}
