package model;

import java.io.Serializable;

/** Chi tiet don hang, luu snapshot ten + gia tai thoi diem dat. */
public class OrderDetail_24133065 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderDetailId;
    private int orderId;
    private int bookid;
    private String productName;
    private double price;
    private int quantity;

    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getBookid() { return bookid; }
    public void setBookid(int bookid) { this.bookid = bookid; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return price * quantity; }
}
