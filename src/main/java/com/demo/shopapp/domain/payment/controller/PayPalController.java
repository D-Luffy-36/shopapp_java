package com.demo.shopapp.domain.payment.controller;

import com.demo.shopapp.domain.order.entity.Order;
import com.demo.shopapp.domain.order.entity.OrderStatus;
import com.demo.shopapp.domain.order.service.OrderService;
import com.demo.shopapp.domain.payment.dto.request.PayPalDTO;
import com.demo.shopapp.domain.payment.dto.response.PayPalResponse;
import com.demo.shopapp.domain.payment.dto.response.PaymentResponse;
import com.demo.shopapp.domain.payment.dto.response.UrlResponse;
import com.demo.shopapp.domain.payment.repository.PaymentRepository;
import com.demo.shopapp.domain.payment.service.PayPalService;
import com.demo.shopapp.shared.response.ResponseObject;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor
public class PayPalController {

    private final APIContext apiContext;
    private final OrderService orderService;
    private final PayPalService payPalService;
    private final PaymentRepository paymentRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PayPalController.class);


    @PostMapping("/paypal")
    public ResponseEntity<UrlResponse> createPaypalPayment(@RequestBody PayPalDTO payPalDTO) throws Exception {
        // Lấy thông tin người dùng từ JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String identifier = authentication.getName();
        logger.info("Email người dùng: {}", identifier);

        // Tạo thanh toán PayPal
        PaymentResponse paymentResponse = payPalService.createPayment(
                payPalDTO.getAmount(),
                payPalDTO.getCurrency(),
                payPalDTO.getDescription() + " - User: " + identifier
        );

        String paymentId = paymentResponse.getPaymentId();
        String approvalUrl = paymentResponse.getApprovalUrl();

        // Kiểm tra paymentId và approvalUrl
        if (paymentId == null || approvalUrl == null) {
            logger.error("Không thể lấy paymentId hoặc approvalUrl từ PayPal");
            throw new Exception("Không thể lấy paymentId hoặc approvalUrl từ PayPal");
        }

        // Tìm Order dựa trên orderId từ PayPalDTO
        Long orderId = payPalDTO.getOrderId();
        if (orderId == null) {
            logger.error("orderId không được cung cấp trong PayPalDTO");
            throw new Exception("orderId không được cung cấp trong PayPalDTO");
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            logger.error("Không tìm thấy đơn hàng với ID: {}", orderId);
            throw new Exception("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        // Lưu paymentId vào Order
        order.setPaymentId(paymentId);
        orderService.saveOrder(order);

        logger.info("PayPal approval URL: {}", approvalUrl);
        return ResponseEntity.ok(new UrlResponse(approvalUrl));
    }

    @GetMapping("/paypal-return")
    public ResponseObject<PayPalResponse> completePaypalPayment(
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId,
            @RequestParam(value = "cancel", required = false) Boolean cancel,
            @RequestParam(value = "orderId", required = false) String orderIdParam) {
        try {
            PayPalResponse.PayPalResponseBuilder builder = PayPalResponse.builder();
            String redirectUrl = "http://localhost:4200/orders";
            HttpStatus httpStatus = HttpStatus.OK;
            String message = "";
            Order order = null;
            Long orderId = null;

            // Chuyển đổi orderIdParam thành Long nếu có giá trị hợp lệ
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                try {
                    orderId = Long.parseLong(orderIdParam);
                } catch (NumberFormatException e) {
                    logger.warn("orderId không hợp lệ: {}, sẽ tìm dựa trên paymentId", orderIdParam);
                    if (paymentId != null) {
                        orderId = orderService.findOrderIdByPaymentId(paymentId);
                        if (orderId == null) {
                            logger.error("Không tìm thấy đơn hàng ứng với paymentId: {}", paymentId);
                            return ResponseObject.<PayPalResponse>builder()
                                    .status(HttpStatus.BAD_REQUEST)
                                    .message("Không tìm thấy đơn hàng ứng với paymentId: " + paymentId)
                                    .data(builder.build())
                                    .redirectUrl(redirectUrl)
                                    .build();
                        }
                    } else {
                        logger.error("Thiếu paymentId để tìm orderId");
                        return ResponseObject.<PayPalResponse>builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Thiếu paymentId để tìm orderId")
                                .data(builder.build())
                                .redirectUrl(redirectUrl)
                                .build();
                    }
                }
            } else if (paymentId != null) {
                orderId = orderService.findOrderIdByPaymentId(paymentId);
                if (orderId == null) {
                    logger.error("Không tìm thấy đơn hàng ứng với paymentId: {}", paymentId);
                    return ResponseObject.<PayPalResponse>builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Không tìm thấy đơn hàng ứng với paymentId: " + paymentId)
                            .data(builder.build())
                            .redirectUrl(redirectUrl)
                            .build();
                }
            } else {
                logger.error("Thiếu orderId và paymentId");
                return ResponseObject.<PayPalResponse>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Thiếu orderId và paymentId")
                        .data(builder.build())
                        .redirectUrl(redirectUrl)
                        .build();
            }

            // Trường hợp hủy thanh toán
            if (Boolean.TRUE.equals(cancel)) {
                if (orderId == null) {
                    logger.error("Thiếu orderId khi hủy thanh toán PayPal");
                    return ResponseObject.<PayPalResponse>builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Thiếu orderId khi hủy thanh toán")
                            .data(builder.build())
                            .redirectUrl(redirectUrl)
                            .build();
                }

                Optional<Order> optionalOrder = Optional.ofNullable(orderService.getOrderById(orderId));
                if (optionalOrder.isEmpty()) {
                    logger.error("Không tìm thấy đơn hàng với ID: {}", orderId);
                    return ResponseObject.<PayPalResponse>builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Không tìm thấy đơn hàng với ID: " + orderId)
                            .data(builder.build())
                            .redirectUrl(redirectUrl)
                            .build();
                }

                order = optionalOrder.get();
                orderService.updateOrderStatustPay(orderId, com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.FAILED); // Sửa thành updateOrderStatus
                logger.info("Đơn hàng {} được cập nhật trạng thái CANCELLED", orderId);

                // Lưu Payment với trạng thái FAILED
                com.demo.shopapp.domain.payment.entity.Payment paymentEntity = new com.demo.shopapp.domain.payment.entity.Payment();
                paymentEntity.setId(paymentId); // Sử dụng paymentId từ PayPal
                paymentEntity.setOrder(order);

                paymentEntity.setPaymentMethod("PayPal"); // Gán phương thức thanh toán
                paymentEntity.setAmount(order.getPayment().getAmount()); // Giả định Order có phương thức getTotalAmount()
                paymentEntity.setPaidAt(LocalDateTime.now());
                paymentEntity.setStatus(com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.FAILED);
                paymentRepository.save(paymentEntity);

                return ResponseObject.<PayPalResponse>builder()
                        .status(HttpStatus.NOT_MODIFIED)
                        .message("Thanh toán PayPal bị hủy!")
                        .data(builder.orderId(orderId).orderStatus(OrderStatus.CANCELLED.toString()).build())
                        .redirectUrl(redirectUrl)
                        .build();
            }

            // Trường hợp hoàn tất thanh toán
            if (paymentId == null || payerId == null) {
                logger.error("Thiếu paymentId hoặc PayerID");
                return ResponseObject.<PayPalResponse>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Thiếu paymentId hoặc PayerID")
                        .data(builder.build())
                        .redirectUrl(redirectUrl)
                        .build();
            }

            Payment payment = payPalService.executePayment(paymentId, payerId);
            boolean isSuccess = "approved".equalsIgnoreCase(payment.getState());

            builder.paymentId(payment.getId())
                    .paymentMethod(payment.getPayer().getPaymentMethod())
                    .payerId(payerId)
                    .isSuccess(isSuccess);

            try {
                Transaction transaction = payment.getTransactions().get(0);
                RelatedResources relatedResources = transaction.getRelatedResources().get(0);
                builder.transactionId(relatedResources.getSale().getId())
                        .amount(new BigDecimal(transaction.getAmount().getTotal()));
            } catch (Exception e) {
                logger.error("Lỗi khi lấy thông tin giao dịch: {}", e.getMessage());
                builder.error("Lỗi khi lấy thông tin giao dịch: " + e.getMessage());
            }

            Optional<Order> optionalOrder = Optional.ofNullable(orderService.getOrderById(orderId));
            if (optionalOrder.isEmpty()) {
                logger.error("Không tìm thấy đơn hàng với ID: {}", orderId);
                return ResponseObject.<PayPalResponse>builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("Không tìm thấy đơn hàng với ID: " + orderId)
                        .data(builder.build())
                        .redirectUrl(redirectUrl)
                        .build();
            }

            order = optionalOrder.get();
            builder.orderId(orderId);

            // Tạo và lưu Payment entity
            com.demo.shopapp.domain.payment.entity.Payment paymentEntity = new com.demo.shopapp.domain.payment.entity.Payment();
            paymentEntity.setId(paymentId); // Sử dụng paymentId từ PayPal
            paymentEntity.setOrder(order);

            paymentEntity.setPaymentMethod(payment.getPayer().getPaymentMethod());
            paymentEntity.setAmount(new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal()));
            paymentEntity.setPaidAt(LocalDateTime.now());

            try {
                Transaction transaction = payment.getTransactions().get(0);
                RelatedResources relatedResources = transaction.getRelatedResources().get(0);
                paymentEntity.setTransactionId(relatedResources.getSale().getId());
            } catch (Exception e) {
                logger.warn("Không thể lấy transactionId: {}", e.getMessage());
                paymentEntity.setTransactionId(paymentId); // Fallback sử dụng paymentId
            }

            // Cập nhật trạng thái đơn hàng và thanh toán
            if (isSuccess) {
                orderService.updateOrderStatustPay(orderId, com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.PAID);
                paymentEntity.setStatus(com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.PAID);
                logger.info("Đơn hàng {} được cập nhật trạng thái PAID", orderId);
                message = "Thanh toán PayPal thành công!";
                builder.orderStatus(com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.PAID.toString());
            } else {
                orderService.updateOrderStatustPay(orderId, com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.REFUNDED);
                paymentEntity.setStatus(com.demo.shopapp.domain.payment.entity.Payment.PaymentStatus.FAILED);
                logger.info("Đơn hàng {} được cập nhật trạng thái CANCELLED", orderId);
                message = "Thanh toán PayPal thất bại!";
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                builder.orderStatus(OrderStatus.CANCELLED.toString());
            }

            // Lưu Payment entity vào cơ sở dữ liệu
            paymentRepository.save(paymentEntity);

            return ResponseObject.<PayPalResponse>builder()
                    .status(httpStatus)
                    .message(message)
                    .data(builder.build())
                    .redirectUrl(redirectUrl)
                    .build();

        } catch (Exception e) {
            logger.error("Lỗi khi xử lý thanh toán PayPal: {}", e.getMessage());
            return ResponseObject.<PayPalResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Lỗi khi xử lý thanh toán PayPal: " + e.getMessage())
                    .data(PayPalResponse.builder().error(e.getMessage()).build())
                    .redirectUrl("http://localhost:4200/orders")
                    .build();
        }
    }

    @GetMapping("/paypal-cancel")
    public ResponseObject<Void> paypalCancel() {
        return ResponseObject.<Void>builder()
                .status(HttpStatus.NOT_MODIFIED)
                .message("Thanh toán PayPal bị hủy!")
                .data(null)
                .build();
    }
}