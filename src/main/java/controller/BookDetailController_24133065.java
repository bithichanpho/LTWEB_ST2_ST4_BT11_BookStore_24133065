package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book_24133065;
import model.Rating_24133065;
import service.BookService_24133065;

/**
 * CAU 4: Controller trang chi tiet mot cuon sach
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/book/detail" })
public class BookDetailController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService_24133065 bookService = new BookService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int bookid = 0;
        try {
            bookid = Integer.parseInt(request.getParameter("bookid"));
        } catch (NumberFormatException e) {
            bookid = 0;
        }

        Book_24133065 book = bookService.getBookDetail(bookid);
        if (book == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        List<Rating_24133065> reviews = bookService.getReviews(bookid);

        request.setAttribute("book", book);
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/views/bookDetail.jsp").forward(request, response);
    }
}
