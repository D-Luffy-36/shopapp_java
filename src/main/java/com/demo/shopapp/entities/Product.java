package com.demo.shopapp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    private String description;

    @Column(length = 300)
    private String thumbnail;

    @ManyToOne
    @JoinColumn(name = "category_id" )
    private Category category;


}


