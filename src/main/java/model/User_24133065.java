package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model bang USERS - Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class User_24133065 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String email;
    private String fullname;
    private int phone;
    private String passwd;
    private Timestamp signupDate;
    private Timestamp lastLogin;
    private boolean admin;

    public User_24133065() {
    }

    public User_24133065(String email, String fullname, int phone, String passwd) {
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Timestamp getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Timestamp signupDate) {
        this.signupDate = signupDate;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
