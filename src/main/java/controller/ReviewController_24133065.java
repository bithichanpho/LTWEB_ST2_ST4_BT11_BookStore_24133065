package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User_24133065;
import service.BookService_24133065;

/**
 * CAU 4: Controller them review cho sach (chi thanh vien da dang nhap)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/book/review" })
public class ReviewController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService_24133065 bookService = new BookService_24133065();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Phai dang nhap moi duoc review
        HttpSession session = request.getSession();
        User_24133065 user = (User_24133065) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int bookid = 0;
        int rating = 5;
        try {
            bookid = Integer.parseInt(request.getParameter("bookid"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (NumberFormatException e) {
            bookid = 0;
        }
        String reviewText = request.getParameter("review_text");

        if (reviewText != null && !reviewText.trim().isEmpty() && bookid > 0) {
            bookService.addReview(user.getId(), bookid, rating, reviewText.trim());
        }
        response.sendRedirect(request.getContextPath() + "/book/detail?bookid=" + bookid);
    }
}
