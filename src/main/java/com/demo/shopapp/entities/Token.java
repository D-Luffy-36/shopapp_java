package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


// login có giới hạn thiết bị

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @Column(name = "token_type",nullable = false ,length = 50)
    private String tokenType;

    @Column(name = "is_mobile")
    private Boolean isMobile;

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
