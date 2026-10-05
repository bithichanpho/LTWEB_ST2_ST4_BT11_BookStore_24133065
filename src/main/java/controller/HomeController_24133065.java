package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BookService_24133065;

/**
 * CAU 1 + CAU 3: Controller trang chu - hien thi sach phan trang 3 sp/trang theo tung tac gia
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/home", "/books" })
public class HomeController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService_24133065 bookService = new BookService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Trang hien tai, mac dinh la trang 1
        String keyword = request.getParameter("keyword");
        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        int totalPages = bookService.getTotalPages(keyword);
        if (page < 1) {
            page = 1;
        }
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        // CAU 3: danh sach sach cua trang hien tai, da nhom theo tung tac gia
        request.setAttribute("groupedBooks", bookService.getBooksByPageGroupedByAuthor(page, keyword));
        request.setAttribute("keyword", keyword);
        request.setAttribute("activeMenu", "home");
        request.setAttribute("pageTitle", "Kho sách");
        request.setAttribute("pageSubtitle", keyword == null || keyword.isBlank() ? "Khám phá những cuốn sách nổi bật trong cửa hàng" : "Kết quả tìm kiếm cho: " + keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/views/home.jsp").forward(request, response);
    }
}
