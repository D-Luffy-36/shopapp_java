package com.demo.shopapp.domain.user.service.sendmails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;


    // Tạo mã OTP ngẫu nhiên 6 chữ số
    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // Gửi OTP qua email
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Mã OTP của bạn");
        helper.setText("<h3>Mã OTP của bạn là: <b>" + otp + "</b></h3>", true);

        mailSender.send(message);
    }

}
