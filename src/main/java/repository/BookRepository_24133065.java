package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Author_24133065;
import model.Book_24133065;

/**
 * Repository truy cap bang BOOKS (Data Access Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class BookRepository_24133065 {

    /**
     * Lay danh sach bookid cua mot trang, sap xep theo ten tac gia (CAU 3: phan trang theo tac gia)
     */
    private List<Integer> findBookIdsByPage(String keyword, int offset, int limit) {
        List<Integer> ids = new ArrayList<>();
        String sql;
        boolean filtered = keyword != null && !keyword.trim().isEmpty();
        if (filtered) {
            sql = "SELECT DISTINCT b.bookid "
                + "FROM books b LEFT JOIN book_author ba ON ba.bookid = b.bookid "
                + "LEFT JOIN author a ON a.author_id = ba.author_id "
                + "WHERE b.title LIKE ? OR a.author_name LIKE ? "
                + "ORDER BY b.bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        } else {
            sql = "SELECT b.bookid "
                + "FROM books b "
                + "LEFT JOIN book_author ba ON ba.bookid = b.bookid "
                + "      AND ba.author_id = (SELECT MIN(ba2.author_id) FROM book_author ba2 WHERE ba2.bookid = b.bookid) "
                + "LEFT JOIN author a ON a.author_id = ba.author_id "
                + "ORDER BY CASE WHEN a.author_name IS NULL THEN 1 ELSE 0 END, a.author_name, b.bookid "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        }
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            if (filtered) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(i++, like); ps.setString(i++, like);
            }
            ps.setInt(i++, offset); ps.setInt(i, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("bookid"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ids;
    }

    /** Lay danh sach sach mot trang, co the loc theo ten sach/tac gia. */
    public List<Book_24133065> findByPage(int offset, int limit) {
        return findByPage(null, offset, limit);
    }

    public List<Book_24133065> findByPage(String keyword, int offset, int limit) {
        List<Book_24133065> books = new ArrayList<>();
        List<Integer> ids = findBookIdsByPage(keyword, offset, limit);
        if (ids.isEmpty()) return books;
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) { if (i > 0) in.append(","); in.append("?"); }
        String sql = "SELECT b.*, a.author_id, a.author_name, "
                   + "(SELECT COUNT(*) FROM rating r WHERE r.bookid = b.bookid) AS review_count "
                   + "FROM books b LEFT JOIN book_author ba ON ba.bookid = b.bookid "
                   + "LEFT JOIN author a ON a.author_id = ba.author_id "
                   + "WHERE b.bookid IN (" + in + ") ORDER BY b.bookid, a.author_id";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) ps.setInt(i + 1, ids.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                Book_24133065 current = null;
                while (rs.next()) {
                    int bookid = rs.getInt("bookid");
                    if (current == null || current.getBookid() != bookid) { current = new Book_24133065(); mapBook(current, rs); books.add(current); }
                    int authorId = rs.getInt("author_id");
                    if (authorId > 0) current.getAuthors().add(new Author_24133065(authorId, rs.getString("author_name")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return books;
    }

    public int countAll() { return countAll(null); }

    public int countAll(String keyword) {
        String sql; boolean filtered = keyword != null && !keyword.trim().isEmpty();
        if (filtered) sql = "SELECT COUNT(DISTINCT b.bookid) FROM books b LEFT JOIN book_author ba ON ba.bookid = b.bookid LEFT JOIN author a ON a.author_id = ba.author_id WHERE b.title LIKE ? OR a.author_name LIKE ?";
        else sql = "SELECT COUNT(*) FROM books";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtered) { String like = "%" + keyword.trim() + "%"; ps.setString(1, like); ps.setString(2, like); }
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Tim mot sach theo bookid (CAU 4: trang chi tiet sach)
     */
    public Book_24133065 findById(int bookid) {
        String sql = "SELECT b.*, a.author_id, a.author_name, "
                   + "(SELECT COUNT(*) FROM rating r WHERE r.bookid = b.bookid) AS review_count "
                   + "FROM books b "
                   + "LEFT JOIN book_author ba ON ba.bookid = b.bookid "
                   + "LEFT JOIN author a ON a.author_id = ba.author_id "
                   + "WHERE b.bookid = ? "
                   + "ORDER BY a.author_id";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                Book_24133065 book = null;
                while (rs.next()) {
                    if (book == null) {
                        book = new Book_24133065();
                        mapBook(book, rs);
                    }
                    int authorId = rs.getInt("author_id");
                    if (authorId > 0) {
                        book.getAuthors().add(new Author_24133065(authorId, rs.getString("author_name")));
                    }
                }
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CAU 6: Them sach moi, tra ve bookid vua sinh
     */
    public int insert(Book_24133065 book) {
        String sql = "INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindBook(ps, book);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * CAU 6: Cap nhat thong tin sach
     */
    public boolean update(Book_24133065 book) {
        String sql = "UPDATE books SET isbn = ?, title = ?, publisher = ?, price = ?, "
                   + "description = ?, publish_date = ?, cover_image = ?, quantity = ? "
                   + "WHERE bookid = ?";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindBook(ps, book);
            ps.setInt(9, book.getBookid());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * CAU 6: Xoa sach (phai xoa du lieu phu thuoc trong rating va book_author truoc)
     */
    public boolean delete(int bookid) {
        Connection con = null;
        try {
            con = DBConnection_24133065.getConnection();
            con.setAutoCommit(false);
            // Khong cho xoa sach da nam trong gio hang/don hang vi lich su don phai giu snapshot.
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM cart_items WHERE bookid = ?) + (SELECT COUNT(*) FROM order_details WHERE bookid = ?)") ) {
                check.setInt(1, bookid);
                check.setInt(2, bookid);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        return false;
                    }
                }
            }
            try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM rating WHERE bookid = ?")) {
                ps1.setInt(1, bookid);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM book_author WHERE bookid = ?")) {
                ps2.setInt(1, bookid);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = con.prepareStatement("DELETE FROM books WHERE bookid = ?")) {
                ps3.setInt(1, bookid);
                ps3.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ignore) { }
            }
            e.printStackTrace();
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ignore) { }
            }
        }
        return false;
    }

    private void mapBook(Book_24133065 book, ResultSet rs) throws Exception {
        book.setBookid(rs.getInt("bookid"));
        book.setIsbn(rs.getInt("isbn"));
        book.setTitle(rs.getString("title"));
        book.setPublisher(rs.getString("publisher"));
        book.setPrice(rs.getDouble("price"));
        book.setDescription(rs.getString("description"));
        book.setPublishDate(rs.getString("publish_date"));
        book.setCoverImage(rs.getString("cover_image"));
        book.setQuantity(rs.getInt("quantity"));
        book.setReviewCount(rs.getInt("review_count"));
    }

    private void bindBook(PreparedStatement ps, Book_24133065 book) throws Exception {
        ps.setInt(1, book.getIsbn());
        ps.setString(2, book.getTitle());
        ps.setString(3, book.getPublisher());
        ps.setDouble(4, book.getPrice());
        ps.setString(5, book.getDescription());
        if (book.getPublishDate() == null || book.getPublishDate().isEmpty()) {
            ps.setNull(6, java.sql.Types.DATE);
        } else {
            ps.setDate(6, java.sql.Date.valueOf(book.getPublishDate()));
        }
        ps.setString(7, book.getCoverImage());
        ps.setInt(8, book.getQuantity());
    }
}
