package model;

import java.io.Serializable;

/**
 * Model bang RATING (review cua sach) - Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class Rating_24133065 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userid;
    private int bookid;
    private int rating;
    private String reviewText;

    // Ten nguoi review (lay tu bang users de hien thi)
    private String reviewerName;

    public Rating_24133065() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
