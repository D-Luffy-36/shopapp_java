package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "social_accounts")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "token_type",nullable = false ,length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean revoked;

    @Column(nullable = false)
    private Boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

//
//CREATE TABLE [tokens] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//  [token] varchar(255) UNIQUE NOT NULL,
//  [token_type] varchar(50) NOT NULL,
//  [expiration_date] DATETIME,
//        [revoked] BIT NOT NULL,
//        [expired] BIT NOT NULL,
//        [user_id] BIGINT
//)
//GO