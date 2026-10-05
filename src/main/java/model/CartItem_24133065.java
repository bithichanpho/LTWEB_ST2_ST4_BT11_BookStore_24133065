package model;

import java.io.Serializable;

/** Gio hang - mot dong san pham cua User. */
public class CartItem_24133065 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int cartItemId;
    private int userId;
    private int bookid;
    private String title;
    private double price;
    private String coverImage;
    private int quantity;
    private int stock;

    public int getCartItemId() { return cartItemId; }
    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookid() { return bookid; }
    public void setBookid(int bookid) { this.bookid = bookid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getSubtotal() { return price * quantity; }
}
