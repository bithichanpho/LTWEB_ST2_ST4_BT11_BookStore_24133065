package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Author_24133065;
import model.Book_24133065;
import service.BookService_24133065;

/**
 * CAU 6: Controller CRUD (tao, xem, cap nhat, xoa) cho bang Books co phan trang
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
@WebServlet(urlPatterns = { "/admin/books", "/admin/book/add", "/admin/book/edit", "/admin/book/delete" })
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,      // toi da 5MB / anh
        maxRequestSize = 10 * 1024 * 1024
)
public class AdminBookController_24133065 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService_24133065 bookService = new BookService_24133065();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/books":
                // ---------- XEM DANH SACH (co phan trang 3 sp/trang) ----------
                int page = getPage(request);
                int totalPages = bookService.getTotalPages();
                if (page > totalPages && totalPages > 0) page = totalPages;
                request.setAttribute("books", bookService.getBooksByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("pageTitle", "Quản lý sách");
                request.setAttribute("pageSubtitle", "Quản lý danh mục sách, hình ảnh và tồn kho");
                request.setAttribute("activeMenu", "admin-books");
                request.getRequestDispatcher("/views/admin/books.jsp").forward(request, response);
                break;

            case "/admin/book/add":
                // ---------- FORM THEM SACH ----------
                request.setAttribute("authors", bookService.getAllAuthors());
                request.setAttribute("pageTitle", "Thêm sách mới");
                request.setAttribute("pageSubtitle", "Tạo một đầu sách mới trong kho");
                request.setAttribute("activeMenu", "admin-book-add");
                request.getRequestDispatcher("/views/admin/bookForm.jsp").forward(request, response);
                break;

            case "/admin/book/edit":
                // ---------- FORM CAP NHAT SACH ----------
                int bookid = parseInt(request.getParameter("bookid"));
                Book_24133065 book = bookService.getBookDetail(bookid);
                if (book == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/books");
                    return;
                }
                request.setAttribute("book", book);
                request.setAttribute("authors", bookService.getAllAuthors());
                request.setAttribute("pageTitle", "Cập nhật sách");
                request.setAttribute("pageSubtitle", "Chỉnh sửa thông tin sách và tồn kho");
                request.setAttribute("activeMenu", "admin-books");
                request.getRequestDispatcher("/views/admin/bookForm.jsp").forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/books");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/admin/book/delete".equals(path)) {
            // ---------- XOA SACH ----------
            int bookid = parseInt(request.getParameter("bookid"));
            boolean deleted = bookService.deleteBook(bookid);
            response.sendRedirect(request.getContextPath() + "/admin/books?deleted=" + (deleted ? "1" : "0"));
            return;
        }

        // ---------- TAO HOAC CAP NHAT SACH ----------
        String action = "/admin/book/add".equals(path) ? "add" : "edit";

        int bookid = parseInt(request.getParameter("bookid"));
        Book_24133065 book = new Book_24133065();
        book.setBookid(bookid);
        book.setIsbn(parseInt(request.getParameter("isbn")));
        book.setTitle(request.getParameter("title"));
        book.setPublisher(request.getParameter("publisher"));
        book.setPrice(parseDouble(request.getParameter("price")));
        book.setDescription(request.getParameter("description"));
        book.setPublishDate(request.getParameter("publish_date"));
        book.setQuantity(parseInt(request.getParameter("quantity")));

        // ---------- Anh bia: upload tu may nguoi dung ----------
        String oldCoverImage = request.getParameter("oldCoverImage"); // anh cu, dung khi sua ma khong doi anh
        String uploadedFileName = saveUploadedCoverImage(request);
        if (uploadedFileName != null) {
            book.setCoverImage(uploadedFileName);
        } else if (oldCoverImage != null && !oldCoverImage.trim().isEmpty()) {
            book.setCoverImage(oldCoverImage);
        }

        // Danh sach tac gia duoc chon
        List<Integer> authorIds = new ArrayList<>();
        String[] ids = request.getParameterValues("authorIds");
        if (ids != null) {
            for (String id : ids) {
                authorIds.add(parseInt(id));
            }
        }
        String newAuthorName = request.getParameter("newAuthorName");

        // Kiem tra du lieu bat buoc
        String error = null;
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            error = "Vui long nhap tieu de sach!";
        } else if (authorIds.isEmpty()
                && (newAuthorName == null || newAuthorName.trim().isEmpty())) {
            error = "Vui long chon it nhat mot tac gia!";
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("book", book);
            request.setAttribute("authors", bookService.getAllAuthors());
            request.getRequestDispatcher("/views/admin/bookForm.jsp").forward(request, response);
            return;
        }

        boolean ok;
        if ("add".equals(action)) {
            ok = bookService.addBook(book, authorIds, newAuthorName);
        } else {
            ok = bookService.updateBook(book, authorIds, newAuthorName);
        }
        response.sendRedirect(request.getContextPath() + "/admin/books?saved=" + (ok ? "1" : "0"));
    }

    /**
     * Luu file anh bia nguoi dung upload vao thu muc /image cua webapp.
     * Tra ve ten file da luu (dung cho cot cover_image trong DB),
     * hoac null neu nguoi dung khong chon file nao.
     */
    private String saveUploadedCoverImage(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("coverImageFile");
        if (filePart == null || filePart.getSize() <= 0) {
            return null;
        }

        String submittedName = filePart.getSubmittedFileName();
        if (submittedName == null || submittedName.trim().isEmpty()) {
            return null;
        }

        // Lay phan mo rong (.png, .jpg, ...) va sinh ten file duy nhat de tranh trung
        String ext = "";
        int dotIdx = submittedName.lastIndexOf('.');
        if (dotIdx >= 0) {
            ext = submittedName.substring(dotIdx);
        }
        String savedFileName = "cover_" + UUID.randomUUID().toString().replace("-", "") + ext;

        String imageDir = getServletContext().getRealPath("/image");
        Path targetDir = Path.of(imageDir);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        Path targetFile = targetDir.resolve(savedFileName);

        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return savedFileName;
    }

    private int getPage(HttpServletRequest request) {
        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        return page < 1 ? 1 : page;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }
}