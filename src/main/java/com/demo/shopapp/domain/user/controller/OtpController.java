package com.demo.shopapp.domain.user.controller;

import com.demo.shopapp.domain.user.service.sendmails.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api.prefix}/otp")
public class OtpController {
    private final EmailService emailService;

    public OtpController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendOtp(@RequestParam String email) {
        try {
            String otp = emailService.generateOTP();
            emailService.sendOtpEmail(email, otp);
            return "OTP đã gửi thành công!";
        } catch (MessagingException e) {
            return "Lỗi khi gửi OTP: " + e.getMessage();
        }
    }
}
