package model;

import java.io.Serializable;

/**
 * Model bang AUTHOR - Sinh vien: Tran Thi Phuong Trang - MSSV: 24133065
 */
public class Author_24133065 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int authorId;
    private String authorName;
    private String dateOfBirth;

    public Author_24133065() {
    }

    public Author_24133065(int authorId, String authorName) {
        this.authorId = authorId;
        this.authorName = authorName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
