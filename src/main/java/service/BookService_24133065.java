package service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Author_24133065;
import model.Book_24133065;
import model.Rating_24133065;
import repository.AuthorRepository_24133065;
import repository.BookRepository_24133065;
import repository.RatingRepository_24133065;

/**
 * Nghiep vu ve sach: phan trang theo tac gia, chi tiet, review, CRUD (Business Layer)
 * Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class BookService_24133065 {

    public static final int PAGE_SIZE = 3; // CAU 3: phan trang 3 san pham / trang

    private final BookRepository_24133065 bookRepository = new BookRepository_24133065();
    private final AuthorRepository_24133065 authorRepository = new AuthorRepository_24133065();
    private final RatingRepository_24133065 ratingRepository = new RatingRepository_24133065();

    /**
     * CAU 3: Lay sach cua mot trang, nhom theo tac gia (LinkedHashMap giu thu tu theo ten tac gia)
     */
    public Map<String, List<Book_24133065>> getBooksByPageGroupedByAuthor(int page) {
        return getBooksByPageGroupedByAuthor(page, null);
    }

    public Map<String, List<Book_24133065>> getBooksByPageGroupedByAuthor(int page, String keyword) {
        int offset = (page - 1) * PAGE_SIZE;
        List<Book_24133065> books = bookRepository.findByPage(keyword, offset, PAGE_SIZE);
        Map<String, List<Book_24133065>> grouped = new LinkedHashMap<>();
        for (Book_24133065 b : books) {
            String key = b.getAuthorNames().isEmpty() ? "Khong xac dinh" : b.getAuthorNames();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(b);
        }
        return grouped;
    }

    /**
     * CAU 3: Tong so trang = ceil(tong so sach / kich thuoc trang)
     */
    public int getTotalPages() { return getTotalPages(null); }

    public int getTotalPages(String keyword) {
        int total = bookRepository.countAll(keyword);
        return (int) Math.ceil((double) total / PAGE_SIZE);
    }

    /**
     * CAU 4: Lay thong tin chi tiet mot cuon sach
     */
    public Book_24133065 getBookDetail(int bookid) {
        return bookRepository.findById(bookid);
    }

    /**
     * CAU 4: Lay danh sach review cua mot cuon sach
     */
    public List<Rating_24133065> getReviews(int bookid) {
        return ratingRepository.findByBook(bookid);
    }

    /**
     * CAU 4: Them review moi cho sach
     */
    public boolean addReview(int userid, int bookid, int rating, String reviewText) {
        Rating_24133065 r = new Rating_24133065();
        r.setUserid(userid);
        r.setBookid(bookid);
        r.setRating(rating);
        r.setReviewText(reviewText);
        return ratingRepository.insert(r);
    }

    /**
     * CAU 6: Danh sach sach cua mot trang dung cho CRUD admin
     */
    public List<Book_24133065> getBooksByPage(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return bookRepository.findByPage(offset, PAGE_SIZE);
    }

    /**
     * CAU 6: Them sach moi (kem cac tac gia, co the them tac gia moi)
     */
    public boolean addBook(Book_24133065 book, List<Integer> authorIds, String newAuthorName) {
        int bookid = bookRepository.insert(book);
        if (bookid <= 0) {
            return false;
        }
        saveAuthors(bookid, authorIds, newAuthorName);
        return true;
    }

    /**
     * CAU 6: Cap nhat sach (xac nhan lai danh sach tac gia)
     */
    public boolean updateBook(Book_24133065 book, List<Integer> authorIds, String newAuthorName) {
        boolean ok = bookRepository.update(book);
        if (ok) {
            authorRepository.deleteLinksByBook(book.getBookid());
            saveAuthors(book.getBookid(), authorIds, newAuthorName);
        }
        return ok;
    }

    /**
     * CAU 6: Xoa sach
     */
    public boolean deleteBook(int bookid) {
        return bookRepository.delete(bookid);
    }

    /**
     * Danh sach tac gia dung cho form them/sua sach
     */
    public List<Author_24133065> getAllAuthors() {
        return authorRepository.findAll();
    }

    private void saveAuthors(int bookid, List<Integer> authorIds, String newAuthorName) {
        if (authorIds != null) {
            for (int authorId : authorIds) {
                authorRepository.linkBookAuthor(bookid, authorId);
            }
        }
        if (newAuthorName != null && !newAuthorName.trim().isEmpty()) {
            int newId = authorRepository.insert(newAuthorName.trim());
            if (newId > 0) {
                authorRepository.linkBookAuthor(bookid, newId);
            }
        }
    }
}
