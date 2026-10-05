package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User_24133065;
import service.MailService_24133065;
import service.UserService_24133065;

/**
 * CAU 2: Controller dang ky tai khoan - gui OTP qua email de kich hoat
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/register" })
public class RegisterController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserService_24133065 userService = new UserService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String fullname = request.getParameter("fullname");
        String phoneStr = request.getParameter("phone");
        String passwd = request.getParameter("passwd");
        String confirm = request.getParameter("confirm");

        // ---------- Kiem tra du lieu dau vao ----------
        String error = null;
        int phone = 0;
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$")) {
            error = "Email khong dung dinh dang!";
        } else if (fullname == null || fullname.trim().isEmpty()) {
            error = "Vui long nhap ho ten!";
        } else if (phoneStr == null || !phoneStr.matches("\\d{9,10}")) {
            error = "So dien thoai phai gom 9 - 10 chu so!";
        } else if (userService.emailExists(email.trim())) {
            error = "Email nay da duoc dang ky!";
        } else if (passwd == null || passwd.length() < 6) {
            error = "Mat khau phai co it nhat 6 ky tu!";
        } else if (!passwd.equals(confirm)) {
            error = "Xac nhan mat khau khong khop!";
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("email", email);
            request.setAttribute("fullname", fullname);
            request.setAttribute("phone", phoneStr);
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        try {
            phone = Integer.parseInt(phoneStr.trim());
        } catch (NumberFormatException e) {
            phone = 0;
        }

        // ---------- Luu thong tin dang ky tam thoi vao SESSION cho den khi kich hoat OTP ----------
        HttpSession session = request.getSession();
        User_24133065 pending = new User_24133065(email.trim(), fullname.trim(), phone,
                UserService_24133065.md5(passwd));
        session.setAttribute("pendingUser", pending);

        // ---------- Tao OTP va gui qua email ----------
        String otp = userService.generateOtp();
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpireTime", System.currentTimeMillis() + 5 * 60 * 1000);
        MailService_24133065.sendOtpEmail(email.trim(), otp);

        response.sendRedirect(request.getContextPath() + "/verify");
    }
}
