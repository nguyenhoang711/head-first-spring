package com.github.nguyenhoang711.head_first_spring.service;


import com.github.nguyenhoang711.head_first_spring.constant.OtpType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text, boolean isHtml) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtml);

            log.info("Sending email to {}", to);
            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw e;
        }
    }

    public void sendOtpEmail(String to, String otp, OtpType otpType) throws MessagingException {
        switch (otpType) {
            case RESET_PASSWORD:
                sendResetPasswordOtp(to, otp);
                break;
            case REGISTRATION:
                sendRegistrationOtp(to, otp);
                break;
            case LOGIN:
                sendLoginOtp(to, otp);
                break;
            default:
                log.error("Unknown OTP type: {}", otpType);
                throw new IllegalArgumentException("Không hỗ trợ loại OTP này");
        }
    }

    private void sendRegistrationOtp(String to, String otp) throws MessagingException {
        String subject = "Mã OTP xác thực tài khoản của bạn";
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif;'>" +
                        "<h2>Xác thực tài khoản</h2>" +
                        "<p>Mã OTP của bạn để xác thực tài khoản là:</p>" +
                        "<h1 style='color: #4285f4; font-size: 32px; letter-spacing: 2px;'>%s</h1>" +
                        "<p>Mã này có hiệu lực trong 5 phút.</p>" +
                        "<p>Nếu bạn không yêu cầu xác thực tài khoản, vui lòng bỏ qua email này.</p>" +
                        "</div>",
                otp);

        sendEmail(to, subject, htmlContent, true);
    }

    private void sendLoginOtp(String to, String otp) throws MessagingException {
        String subject = "Mã OTP đăng nhập của bạn";
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif;'>" +
                        "<h2>Đăng nhập tài khoản</h2>" +
                        "<p>Mã OTP của bạn để đăng nhập là:</p>" +
                        "<h1 style='color: #4285f4; font-size: 32px; letter-spacing: 2px;'>%s</h1>" +
                        "<p>Mã này có hiệu lực trong 5 phút.</p>" +
                        "<p>Nếu bạn không yêu cầu đăng nhập, vui lòng bỏ qua email này.</p>" +
                        "</div>",
                otp);

        sendEmail(to, subject, htmlContent, true);
    }

    private void sendResetPasswordOtp(String to, String otp) throws MessagingException {
        String subject = "Mã OTP đổi mật khẩu của bạn";
        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif;'>" +
                        "<h2>Yêu cầu đổi mật khẩu</h2>" +
                        "<p>Mã OTP của bạn để đổi mật khẩu là:</p>" +
                        "<h1 style='color: #4285f4; font-size: 32px; letter-spacing: 2px;'>%s</h1>" +
                        "<p>Mã này có hiệu lực trong 5 phút.</p>" +
                        "<p>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này.</p>" +
                        "</div>",
                otp);

        sendEmail(to, subject, htmlContent, true);
    }
}
