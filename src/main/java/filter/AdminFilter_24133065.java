package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User_24133065;

/**
 * CAU 1: Bo loc kiem tra vai tro ADMIN cho cac duong dan /admin/*
 * Chi admin moi duoc vao trang quan tri
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class AdminFilter_24133065 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User_24133065 user = (session != null) ? (User_24133065) session.getAttribute("user") : null;

        if (user != null && user.isAdmin()) {
            // Dung vai tro admin -> cho phep truy cap
            chain.doFilter(request, response);
        } else {
            // Khong phai admin -> chuyen ve trang dang nhap
            res.sendRedirect(req.getContextPath() + "/login?adminRequired=1");
        }
    }
}
