package service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Gui ma OTP kich hoat tai khoan qua email (Business Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class MailService_24133065 {

    private static final String FROM_EMAIL = "tranthiphuongtrang2711@gmail.com";
    private static final String APP_PASSWORD = "iyqh vojp vaic szfn";
    private static final String FROM_NAME = "BookStore 24133065";

    /**
     * Cau hinh ket noi SMTP Gmail (port 587, TLS)
     */
    private static Session getMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }

    /**
     * Gui email chua ma OTP kich hoat tai khoan den dia chi nguoi dung
     */
    public static void sendOtpEmail(String toEmail, String otp) {
        try {
            Session session = getMailSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("BookStore - Ma OTP kich hoat tai khoan cua ban", "UTF-8");

            String content = "<div style='font-family:Arial,sans-serif;max-width:480px;margin:auto'>"
                    + "<h2 style='color:#2c3e50'>Kich hoat tai khoan BookStore</h2>"
                    + "<p>Xin ch&agrave;o,</p>"
                    + "<p>B&#7841;n v&#7915;a &#273;&#259;ng k&yacute; t&agrave;i kho&#7843;n t&#7841;i BookStore."
                    + " M&atilde; OTP &#273;&#7875; k&iacute;ch ho&#7841;t t&agrave;i kho&#7843;n c&#7911;a b&#7841;n l&agrave;:</p>"
                    + "<div style='font-size:28px;font-weight:bold;color:#4CAF50;letter-spacing:6px;"
                    + "background:#f4f4f4;padding:12px 20px;display:inline-block;border-radius:6px'>"
                    + otp + "</div>"
                    + "<p style='margin-top:16px'>M&atilde; c&oacute; hi&#7879;u l&#7921;c trong <b>5 ph&uacute;t</b>."
                    + " Vui l&ograve;ng kh&ocirc;ng chia s&#7867; m&atilde; n&agrave;y cho b&#7845;t k&#7923; ai.</p>"
                    + "<p style='color:gray;font-size:12px'>N&#7871;u b&#7841;n kh&ocirc;ng &#273;&#259;ng k&yacute;, h&atilde;y b&#7887; qua email n&agrave;y.</p>"
                    + "</div>";
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[BookStore_24133065] Da gui OTP " + otp + " den " + toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Khong the gui email OTP: " + e.getMessage(), e);
        }
    }
}
