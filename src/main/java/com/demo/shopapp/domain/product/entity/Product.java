package com.demo.shopapp.domain.product.entity;

import com.demo.shopapp.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
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

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<ProductImage> images;

}


