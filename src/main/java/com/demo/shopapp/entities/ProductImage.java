package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "product_images")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ProductImage {
    public static final int MAX_IMAGES = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 300)
    private String imageUrl;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;
}

//CREATE TABLE product_images (
//        id INT PRIMARY KEY IDENTITY(1, 1),
//product_id BIGINT,
//FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
//image_url VARCHAR(300)
//);
//GO