package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByGoogleAccountId(Long googleId);
    boolean existsByFaceBookAccountId(Long facebookId);

    Optional<User> findByPhoneNumber(String phoneNumber);

}
