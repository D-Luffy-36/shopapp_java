package com.demo.shopapp.domain.payment.controller;


import com.demo.shopapp.domain.payment.dto.response.VnPayUrlResponse;
import com.demo.shopapp.domain.payment.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.logging.Logger;

@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnPayService;
    private static final Logger logger = Logger.getLogger(VNPayController.class.getName());

    @GetMapping("/create")
    public String showPaymentForm() {
        return "payment"; // Trang form thanh toán
    }

    /**
     * Nhận JSON body chứa VNPayRequest, sinh URL thanh toán và redirect.
     */
    @PostMapping(path = "/vnpay", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VnPayUrlResponse> createVNPayPayment(
            @Valid @RequestBody VnPayUrlResponse.VNPayRequest vnPayRequest,
            HttpServletRequest request) {
        // Lấy thông tin người dùng từ JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Giả sử username là userId

        logger.info("vnPayRequest: " + vnPayRequest.getOrderInfo());

        // 2. Build baseUrl cho returnUrl
        String baseUrl = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort();

        // 3. Gắn thêm userId vào orderInfo
        String orderInfoWithUser = vnPayRequest.getOrderInfo()
                + " - User: " + userId;


        // 4. Gọi service, truyền amount và orderInfo đã build
        String vnpayUrl = vnPayService.createOrder(
                request,
                vnPayRequest.getAmount(),
                orderInfoWithUser,
                baseUrl
        );
        logger.info("VNPay URL: " + vnpayUrl);

        return ResponseEntity
                .ok()
                .body(new VnPayUrlResponse(vnpayUrl));
    }


    @GetMapping("/vnpay-return")
    public String handleVNPayReturn(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice != null ? Integer.parseInt(totalPrice) / 100 : 0); // Chuyển về đơn vị VNĐ
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "paymentSuccess" : "paymentFail";
    }

}
