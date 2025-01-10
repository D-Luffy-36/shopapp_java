package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_id",length = 50)
    private String providerId;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}



//CREATE TABLE [social_accounts] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//      [provider] varchar(50) NOT NULL,
//      [provider_id] varchar(50) NOT NULL,
//      [email] varchar(150) NOT NULL,
//      [name] varchar(100) NOT NULL,
//      [user_id] BIGINT
//)
//GO
