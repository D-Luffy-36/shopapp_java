package com.demo.shopapp.domain.user.repository;

import com.demo.shopapp.domain.user.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
  Optional<SocialAccount> findByProviderAndProviderId(String provider, String providerId);
}