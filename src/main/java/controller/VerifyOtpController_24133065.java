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
 * CAU 2: Controller xac thuc ma OTP kich hoat tai khoan + gui lai OTP
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/verify", "/resend-otp" })
public class VerifyOtpController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserService_24133065 userService = new UserService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User_24133065 pending = (User_24133065) session.getAttribute("pendingUser");
        if (pending == null) {
            // Chua co thong tin dang ky -> quay lai trang dang ky
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        // Duong dan /resend-otp: gui lai ma OTP moi qua email
        if ("/resend-otp".equals(request.getServletPath())) {
            String otp = userService.generateOtp();
            session.setAttribute("otp", otp);
            session.setAttribute("otpExpireTime", System.currentTimeMillis() + 5 * 60 * 1000);
            MailService_24133065.sendOtpEmail(pending.getEmail(), otp);
            request.setAttribute("message", "Da gui lai ma OTP moi den email cua ban!");
            request.getRequestDispatcher("/views/verify.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/views/verify.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User_24133065 pending = (User_24133065) session.getAttribute("pendingUser");
        if (pending == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String otpInput = request.getParameter("otp");
        String otpSaved = (String) session.getAttribute("otp");
        Long expireTime = (Long) session.getAttribute("otpExpireTime");

        // ---------- Kiem tra OTP het han ----------
        if (expireTime == null || System.currentTimeMillis() > expireTime) {
            request.setAttribute("error", "Ma OTP da het han! Vui long gui lai ma moi.");
            request.getRequestDispatcher("/views/verify.jsp").forward(request, response);
            return;
        }

        // ---------- Kiem tra ma OTP nhap vao ----------
        if (otpSaved == null || !otpSaved.equals(otpInput)) {
            request.setAttribute("error", "Ma OTP khong dung! Vui long thu lai.");
            request.getRequestDispatcher("/views/verify.jsp").forward(request, response);
            return;
        }

        // ---------- OTP dung: kich hoat tai khoan, luu vao database ----------
        boolean ok = userService.activateUser(pending);
        session.removeAttribute("pendingUser");
        session.removeAttribute("otp");
        session.removeAttribute("otpExpireTime");

        if (!ok) {
            request.setAttribute("error", "Kich hoat tai khoan that bai, vui long thu lai!");
            request.getRequestDispatcher("/views/verify.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login?registered=1");
    }
}
