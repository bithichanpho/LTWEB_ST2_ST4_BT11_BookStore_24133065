package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model bang BOOKS - Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class Book_24133065 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookid;
    private int isbn;
    private String title;
    private String publisher;
    private double price;
    private String description;
    private String publishDate;
    private String coverImage;
    private int quantity;

    // Danh sach tac gia cua sach (quan he nhieu - nhieu qua bang book_author)
    private List<Author_24133065> authors = new ArrayList<>();

    // So luong review cua sach
    private int reviewCount;

    public Book_24133065() {
    }

    public String getAuthorNames() {
        if (authors == null || authors.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(authors.get(i).getAuthorName());
        }
        return sb.toString();
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Author_24133065> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author_24133065> authors) {
        this.authors = authors;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
