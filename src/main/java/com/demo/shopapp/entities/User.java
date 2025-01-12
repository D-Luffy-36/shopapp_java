package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDate;

@Entity
@SuperBuilder
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "phone_number",unique = true, nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "password", nullable = true, length = 255)
    private String password;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "facebook_account_id", unique = true)
    private Long faceBookAccountId;

    @Column(name = "google_account_id", unique = true)
    private Long googleAccountId;

    @Column(name = "is_active")
    private Boolean isActive;

    // Role instance
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}


//CREATE TABLE [users] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//  [fullname] VARCHAR(150),
//  [phone_number] VARCHAR(10) NOT NULL,
//  [address] VARCHAR(255) DEFAULT '',
//  [password] VARCHAR(255) NOT NULL DEFAULT '',
//   [created_at] DATETIME DEFAULT 'GETDATE()',
//        [updated_at] DATETIME DEFAULT 'GETDATE()',
//        [is_active] BIT DEFAULT (1),
//  [date_of_birth] DATE NOT NULL,
//        [facebook_account_id] BIGINT DEFAULT (0),
//  [google_account_id] BIGINT DEFAULT (0),
//  [role_id] BIGINT
//)
//GO