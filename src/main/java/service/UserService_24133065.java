package service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

import model.User_24133065;
import repository.UserRepository_24133065;

/**
 * Nghiep vu tai khoan nguoi dung: dang ky - kich hoat OTP - dang nhap (Business Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class UserService_24133065 {

    private final UserRepository_24133065 userRepository = new UserRepository_24133065();

    /**
     * Kiem tra email da duoc dung de dang ky chua
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Tao ma OTP ngau nhien 6 chu so
     */
    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    /**
     * Kich hoat tai khoan: luu user vao database sau khi xac thuc OTP thanh cong
     */
    public boolean activateUser(User_24133065 user) {
        return userRepository.insert(user);
    }

    /**
     * Dang nhap: kiem tra email + mat khau (MD5), cap nhat last_login khi thanh cong
     */
    public User_24133065 login(String email, String rawPassword) {
        String md5 = md5(rawPassword);
        User_24133065 user = userRepository.findByEmailAndPassword(email, md5);
        if (user != null) {
            userRepository.updateLastLogin(user.getId());
        }
        return user;
    }

    /**
     * Ma hoa mat khau bang MD5 (ket qua 32 ky tu, phu hop passwd VARCHAR(32))
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, digest));
        } catch (Exception e) {
            throw new RuntimeException("Khong the ma hoa MD5", e);
        }
    }
}
