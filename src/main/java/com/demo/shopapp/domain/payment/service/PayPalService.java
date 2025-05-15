package com.demo.shopapp.domain.payment.service;


import com.demo.shopapp.domain.payment.dto.response.PaymentResponse;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.*;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PayPalService.class);

    private final APIContext apiContext;

    @Value("${paypal.return-url}")
    private String returnUrl;

    @Value("${paypal.cancel-url}")
    private String cancelUrl;

    public PaymentResponse createPayment(BigDecimal amount, String currency, String description) throws PayPalRESTException {
        logger.info("Bắt đầu tạo thanh toán PayPal");

        // 1. Khởi tạo đối tượng số tiền thanh toán với đơn vị tiền tệ
        Amount payPalAmount = new Amount();
        payPalAmount.setCurrency(currency);
        payPalAmount.setTotal(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        logger.info("Tạo Amount: currency = {}, total = {}", currency, payPalAmount.getTotal());

        // 2. Tạo một giao dịch với mô tả và số tiền
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(payPalAmount);
        logger.info("Tạo Transaction: description = {}", description);

        // 3. Đưa giao dịch vào danh sách
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        logger.info("Tổng số transaction: {}", transactions.size());

        // 4. Khai báo người thanh toán sử dụng PayPal
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        logger.info("Đặt phương thức thanh toán là PayPal");

        // 5. Tạo đối tượng Payment với các thông tin trên
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        logger.info("Tạo đối tượng Payment với intent = sale");

        // 6. Cấu hình URL chuyển hướng
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(returnUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);
        logger.info("Cấu hình Redirect URL: returnUrl = {}, cancelUrl = {}", returnUrl, cancelUrl);

        // 7. Gửi request tạo payment đến PayPal
        try {
            Payment createdPayment = payment.create(apiContext);
            logger.info("Created PayPal Payment: {}", createdPayment.toJSON());

            // 8. Lấy paymentId và approval URL
            String paymentId = createdPayment.getId();
            String approvalUrl = null;
            for (Links link : createdPayment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    approvalUrl = link.getHref();
                    logger.info("Approval URL: {}", approvalUrl);
                    break;
                }
            }

            if (approvalUrl == null) {
                logger.warn("Approval URL not found in response");
                throw new PayPalRESTException("Approval URL not found in response");
            }

            return new PaymentResponse(paymentId, approvalUrl);
        } catch (PayPalRESTException e) {
            logger.error("PayPal API error: response-code: {}, details: {}", e.getResponsecode(), e.getDetails(), e);
            throw e;
        }
    }

    public String getPaymentIdFromRedirectUrl(String redirectUrl) {
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            System.out.println("Redirect URL is null or empty");
            return null;
        }

        try {
            URL url = new URL(redirectUrl);
            String query = url.getQuery();
            if (query == null) {
                System.out.println("No query parameters found in redirect URL: " + redirectUrl);
                return null;
            }

            Map<String, String> queryParams = new HashMap<>();
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                    queryParams.put(key, value);
                }
            }

            String paymentId = queryParams.get("paymentId");
            if (paymentId == null || paymentId.isEmpty()) {
                System.out.println("paymentId not found in redirect URL: " + redirectUrl);
                return null;
            }

            return paymentId;
        } catch (Exception e) {
            System.out.println("Error extracting paymentId from redirect URL: " + e.getMessage());
            return null;
        }
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }


}
