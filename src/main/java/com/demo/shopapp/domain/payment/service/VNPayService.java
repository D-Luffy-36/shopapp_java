package com.demo.shopapp.domain.payment.service;

import com.demo.shopapp.domain.payment.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private static final Logger logger = LoggerFactory.getLogger(VNPayService.class);

    public String createOrder(HttpServletRequest request, BigDecimal amount, String orderInfo, String baseUrl) {
        if (orderInfo == null || orderInfo.trim().isEmpty()) {
            orderInfo = "Payment transaction";
            logger.warn("orderInfo is null or empty, defaulting to: {}", orderInfo);
        }

        // Chuyển đổi amount thành số nguyên (đã nhân 100)
        long vnpAmount = amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact(); // Chuyển thành long, đảm bảo không có phần thập phân
        logger.info("vnp_Amount: {}", vnpAmount);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = vnPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        logger.info("vnp_IpAddr: {}", vnp_IpAddr);
        String vnp_Locale = "vn";
        String vnp_CurrCode = "VND";
        String vnp_OrderType = "billpayment";

        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnp_TmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));
        vnp_Params.put("vnp_SecureHashType", "HmacSHA512"); // ✅ Thêm dòng này
        // Tính toán vnp_SecureHash

        String vnp_SecureHash = vnPayConfig.hashAllFields(vnp_Params);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
        logger.info("vnp_SecureHash: {}", vnp_SecureHash);

        // Tạo URL với encode
        StringJoiner query = new StringJoiner("&");
        vnp_Params.forEach((key, value) -> query.add(encode(key) + "=" + encode(value)));

        String vnpayUrl = vnPayConfig.getVnp_Url() + "?" + query.toString();
        logger.info("VNPay URL: {}", vnpayUrl);

        return vnpayUrl;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding failed: {}", e.getMessage());
            throw new RuntimeException("Encoding failed", e);
        }
    }

    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnpSecureHash = fields.remove("vnp_SecureHash");
        if (vnpSecureHash == null) {
            logger.error("vnp_SecureHash is missing in callback");
            return -1;
        }

        String hashData = vnPayConfig.hashAllFields(fields);
        logger.info("Callback - Calculated hashData: {}, Received vnp_SecureHash: {}", hashData, vnpSecureHash);

        if (vnpSecureHash.equalsIgnoreCase(hashData)) {
            logger.info("Checksum hợp lệ.");
            String responseCode = fields.get("vnp_ResponseCode");
            return "00".equals(responseCode) ? 1 : 0;
        } else {
            logger.warn("Checksum không hợp lệ.");
            return -1;
        }
    }
}