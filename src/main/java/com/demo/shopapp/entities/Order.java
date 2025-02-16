package com.demo.shopapp.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;
import java.util.List;

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

//    @Column(name = "shipping_address")
//    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    private double discount;

    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    List<OrderDetail> orderDetails;

}

