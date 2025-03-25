package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
  Optional<SocialAccount> findByProviderAndProviderId(String provider, String providerId);
}