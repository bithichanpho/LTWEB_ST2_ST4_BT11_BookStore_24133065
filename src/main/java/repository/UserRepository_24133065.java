package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import model.User_24133065;

/**
 * Repository truy cap bang USERS (Data Access Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class UserRepository_24133065 {

    /**
     * Kiem tra email da ton tai trong bang users hay chua
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Them user moi vao database (goi sau khi kich hoat OTP thanh cong)
     */
    public boolean insert(User_24133065 user) {
        String sql = "INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin) "
                   + "VALUES (?, ?, ?, ?, GETDATE(), 0)";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullname());
            ps.setInt(3, user.getPhone());
            ps.setString(4, user.getPasswd());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Dang nhap: tim user theo email va mat khau (da ma hoa MD5)
     */
    public User_24133065 findByEmailAndPassword(String email, String md5Passwd) {
        String sql = "SELECT id, email, fullname, phone, passwd, signup_date, last_login, is_admin "
                   + "FROM users WHERE email = ? AND passwd = ?";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, md5Passwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cap nhat thoi gian dang nhap gan nhat
     */
    public void updateLastLogin(int id) {
        String sql = "UPDATE users SET last_login = GETDATE() WHERE id = ?";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User_24133065 mapRow(ResultSet rs) throws Exception {
        User_24133065 u = new User_24133065();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setFullname(rs.getString("fullname"));
        u.setPhone(rs.getInt("phone"));
        u.setPasswd(rs.getString("passwd"));
        Timestamp signup = rs.getTimestamp("signup_date");
        u.setSignupDate(signup);
        Timestamp lastLogin = rs.getTimestamp("last_login");
        u.setLastLogin(lastLogin);
        u.setAdmin(rs.getBoolean("is_admin"));
        return u;
    }
}
