package com.demo.shopapp.entities;

import com.demo.shopapp.domain.product.entity.Product;
import jakarta.persistence.*;

public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private float rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likeCount;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int dislikeCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Review parentReview;

}

