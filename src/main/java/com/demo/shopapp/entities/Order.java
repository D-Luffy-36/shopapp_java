package com.demo.shopapp.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;


@Entity
@SuperBuilder
@Table(name = "orders")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User Instance
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname")
    private String fullName;

    private String email;

    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(length = 200)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_money")
    private Double totalMoney;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    private double discount;

    @Column(name = "active")
    private Boolean active;


}

//CREATE TABLE [orders] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//  [user_id] bigint,
//        [fullname] varchar(150),
//  [email] varchar(150),
//  [phone_number] varchar(20) NOT NULL,
//  [address] varchar(200) NOT NULL,
//  [note] varchar(200) DEFAULT ' ',
//        [order_date] DATETIME DEFAULT 'CURRENT_TIMESTAMP',
//        [status] VARCHAR(20) NOT NULL CHECK ([status] IN ('pending', 'processing', 'shipped', 'delivered', 'cancelled')),
//        [total_money] float,
//        [shipping_method] varchar(100),
//  [shipping_address] varchar(200),
//  [shipping_date] date,
//        [tracking_number] varchar(100),
//  [payment_method] varchar(100),
//  [active] BIT DEFAULT (1)
//)
//GO