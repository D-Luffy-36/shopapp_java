package com.demo.shopapp.entities;

import com.demo.shopapp.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Order Object
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    // product Object
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // number of product
    @Column(name = "number_of_product", nullable = false)
    private Integer numberOfProduct;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "price", nullable = false)
    private Double price;

    private String color;
}

