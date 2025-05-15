package com.demo.shopapp.domain.user.repository;

import com.demo.shopapp.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phone);

    @Query("SELECT u FROM User u WHERE " +
            ":keyWord IS NULL OR :keyWord = '' OR " +
            "u.fullName LIKE %:keyWord% OR " +
            "u.phoneNumber LIKE %:keyWord% OR " +
            "u.address LIKE %:keyWord% " +
            "ORDER BY u.createdAt DESC"
    )
    Page<User> findByKeyWord(@Param("keyWord") String keyWord, Pageable pageable);
    Optional<User> findByPhoneNumberOrEmail(String phoneNumber, String email);
}
