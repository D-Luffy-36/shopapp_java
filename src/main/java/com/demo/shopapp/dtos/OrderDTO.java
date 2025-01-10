package com.demo.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("user_id")
    @Min(value = 1, message = "id >= 1")
    private Long userId;

    @JsonProperty("fullname")
    @NotEmpty(message = "Full name cannot be empty")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @JsonProperty("email")
    @Email(message = "Invalid email format")
    private String email;
    @JsonProperty("note")
    @Size(max = 500, message = "Note should not exceed 500 characters")
    private String note;

    @JsonProperty("address")
    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @JsonProperty("phone_number")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    @NotBlank(message = "phone number is required")
    private String phoneNumber;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be greater than or equal to 0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    @NotEmpty(message = "Shipping method cannot be empty")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    @NotEmpty(message = "Shipping address cannot be empty")
    private String shippingAddress;

    @JsonProperty("payment_method")
    @NotEmpty(message = "Payment method cannot be empty")
    private String paymentMethod;
}


//
//CREATE TABLE [orders] (
//  [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//  [user_id] bigint,
//  [fullname] varchar(150),
//  [email] varchar(150),
//  [phone_number] varchar(20) NOT NULL,
//  [address] varchar(200) NOT NULL,
//  [note] varchar(200) DEFAULT ' ',
//  [order_date] DATETIME DEFAULT 'CURRENT_TIMESTAMP',
//  [status] VARCHAR(20) NOT NULL CHECK ([status] IN ('pending', 'processing', 'shipped', 'delivered', 'cancelled')),
//  [total_money] float,
//  [shipping_method] varchar(100),
//  [shipping_address] varchar(200),
//  [shipping_date] date,
//  [tracking_number] varchar(100),
//  [payment_method] varchar(100),
//  [active] BIT DEFAULT (1)
//)
//GO