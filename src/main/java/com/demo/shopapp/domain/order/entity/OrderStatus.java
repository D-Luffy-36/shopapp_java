package com.demo.shopapp.domain.order.entity;


import lombok.Builder;

import java.util.Set;

@Builder
public class OrderStatus {
    public static final String PENDING = "pending";
    public static final String PROCESSING = "processing";
    public static final String SHIPPED = "shipped";
    public static final String DELIVERED = "delivered";
    public static final String CANCELLED = "cancelled";

    public static final Set<String> VALID_STATUSES = Set.of(
            PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    );
}
