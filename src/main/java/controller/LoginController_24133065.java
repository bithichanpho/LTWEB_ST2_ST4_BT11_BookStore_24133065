package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User_24133065;
import service.UserService_24133065;

/**
 * CAU 2: Controller dang nhap co su dung Session
 * Dang nhap voi vai tro user -> trang chu User, vai tro admin -> trang quan tri
 */
@WebServlet(urlPatterns = { "/login" })
public class LoginController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserService_24133065 userService = new UserService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String passwd = request.getParameter("passwd");

        User_24133065 user = userService.login(email, passwd);
        if (user == null) {
            // Dang nhap that bai quay lai trang dang nhap
            request.setAttribute("error", "Email hoac mat khau khong dung!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        // Dang nhap thanh cong: luu thong tin vao SESSION
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        if (user.isAdmin()) {
            // Vai tro admin vao trang quan tri
            response.sendRedirect(request.getContextPath() + "/admin/books");
        } else {
            // Vai tro user vao trang chu cua User
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
