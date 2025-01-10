package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}

//
//CREATE TABLE [roles] (
//        [id] BIGINT PRIMARY KEY,
//        [name] varchar(20) NOT NULL
//)
//GO
