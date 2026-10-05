package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Author_24133065;

/**
 * Repository truy cap bang AUTHOR va BOOK_AUTHOR (Data Access Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class AuthorRepository_24133065 {

    /**
     * Lay danh sach toan bo tac gia
     */
    public List<Author_24133065> findAll() {
        List<Author_24133065> authors = new ArrayList<>();
        String sql = "SELECT author_id, author_name, date_of_birth FROM author ORDER BY author_name";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Author_24133065 a = new Author_24133065();
                a.setAuthorId(rs.getInt("author_id"));
                a.setAuthorName(rs.getString("author_name"));
                a.setDateOfBirth(rs.getString("date_of_birth"));
                authors.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authors;
    }

    /**
     * Them tac gia moi, tra ve author_id vua sinh
     */
    public int insert(String authorName) {
        String sql = "INSERT INTO author (author_name) VALUES (?)";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, authorName);
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
     * Gan tac gia cho sach trong bang trung gian book_author
     */
    public void linkBookAuthor(int bookid, int authorId) {
        String sql = "INSERT INTO book_author (bookid, author_id) VALUES (?, ?)";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.setInt(2, authorId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xoa cac lien ket tac gia cua mot sach trong book_author
     */
    public void deleteLinksByBook(int bookid) {
        String sql = "DELETE FROM book_author WHERE bookid = ?";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
