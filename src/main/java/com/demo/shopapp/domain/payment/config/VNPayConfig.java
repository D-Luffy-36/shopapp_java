package com.demo.shopapp.domain.payment.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Getter
@Setter
public class VNPayConfig {

    @Value("${vnpay.vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.vnp_HashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.vnp_Url}")
    private String vnp_Url;

    @Value("${vnpay.vnp_ReturnUrl}")
    private String vnp_ReturnUrl;

    private static final Logger logger = LoggerFactory.getLogger(VNPayConfig.class);

    @PostConstruct
    public void init() {
        logger.info("Loaded vnp_HashSecret: {}", vnp_HashSecret);
    }

    public String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty() && !fieldName.equals("vnp_SecureHash")) {
                if (!first) {
                    sb.append("&");
                }
                sb.append(fieldName).append("=").append(fieldValue);
                first = false;
            } else {
                logger.warn("Field {} has null, empty, or is vnp_SecureHash, skipped", fieldName);
            }
        }
        String toHash = sb.toString();
        logger.info("Chuỗi cần ký: {}", toHash); // Nâng cấp thành INFO để dễ thấy
        return hmacSHA512(vnp_HashSecret, toHash);
    }

    public String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key or data is null");
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String hashResult = sb.toString();
            logger.info("HMAC-SHA512 input data: {}, key: {}, result: {}", data, key, hashResult);
            return hashResult;
        } catch (Exception ex) {
            logger.error("Lỗi khi tạo chữ ký HMAC SHA512", ex);
            return "";
        }
    }
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
            logger.debug("IP address nhận được: {}", ipAddress);
        } catch (Exception e) {
            ipAddress = "Invalid IP:" + e.getMessage();
            logger.error("Lỗi khi lấy địa chỉ IP", e.getMessage());
        }
        return ipAddress;
    }

    public String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}