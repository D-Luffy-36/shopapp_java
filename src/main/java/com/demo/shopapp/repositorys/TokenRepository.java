package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
