package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Rating_24133065;

/**
 * Repository truy cap bang RATING (review cua sach) - Data Access Layer
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class RatingRepository_24133065 {

    /**
     * CAU 4: Lay danh sach review cua mot sach (kem ten nguoi review)
     */
    public List<Rating_24133065> findByBook(int bookid) {
        List<Rating_24133065> reviews = new ArrayList<>();
        String sql = "SELECT r.userid, r.bookid, r.rating, r.review_text, u.fullname "
                   + "FROM rating r "
                   + "JOIN users u ON u.id = r.userid "
                   + "WHERE r.bookid = ? "
                   + "ORDER BY r.userid DESC";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating_24133065 r = new Rating_24133065();
                    r.setUserid(rs.getInt("userid"));
                    r.setBookid(rs.getInt("bookid"));
                    r.setRating(rs.getInt("rating"));
                    r.setReviewText(rs.getString("review_text"));
                    r.setReviewerName(rs.getString("fullname"));
                    reviews.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * CAU 4: Them review moi cho sach
     */
    public boolean insert(Rating_24133065 rating) {
        String sql = "INSERT INTO rating (userid, bookid, rating, review_text) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection_24133065.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating.getUserid());
            ps.setInt(2, rating.getBookid());
            ps.setInt(3, rating.getRating());
            ps.setString(4, rating.getReviewText());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
