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
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Order Object
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // product Object
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // number of product
    @Column(name = "number_of_product", nullable = false)
    private Integer numberOfProduct;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "total_money", nullable = false)
    private Double totalMoney;

    private String color;
}

//CREATE TABLE [order_details] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//        [order_id] BIGINT,
//        [product_id] BIGINT,
//        [price] float,
//        [number_of_product] int,
//        [total_money] float,
//        [color] varchar(20) DEFAULT ''
//        )
//GO
