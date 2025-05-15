package com.demo.shopapp.domain.payment.entity;

import com.demo.shopapp.domain.order.entity.Order;
import com.demo.shopapp.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "payments") // Chỉ định rõ tên bảng

public class Payment {

    @Id
    @Column(name = "id") // Đảm bảo độ dài phù hợp với PAYID-...
    private String id; // Thay Long thành String để lưu PAYID-...

    @OneToOne(fetch = FetchType.LAZY) // FetchType.LAZY để tối ưu hiệu suất
    @JoinColumn(name = "order_id", nullable = false) // NOT NULL cho order_id
    private Order order;


    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payment_method", length = 100, nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING) // Sử dụng enum để giới hạn giá trị status
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Mặc định là true (hoạt động)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // Enum cho status
    public enum PaymentStatus {
        PAID,
        FAILED,
        PENDING,
        REFUNDED;

        @JsonCreator
        public static PaymentStatus from(String value) {
            return PaymentStatus.valueOf(value.toUpperCase());
        }
    }


    public Payment() {
        this.transactionId = UUID.randomUUID().toString();
    }

    // Getter và Setter


    // Getter và Setter
    public String getId() { // Thay Long thành String
        return id;
    }

    public void setId(String id) { // Thay Long thành String
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Tự động set createdAt và updatedAt trước khi lưu
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}