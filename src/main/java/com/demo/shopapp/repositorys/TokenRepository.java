package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserIdAndExpiredFalseAndRevokedFalse(Long userId);

    Optional<Token> findByToken(String token);
    Optional<Token>  findByRefreshToken(String refreshToken);
}
