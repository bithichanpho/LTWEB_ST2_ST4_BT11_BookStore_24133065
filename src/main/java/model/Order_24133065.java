package model;

import java.io.Serializable;
import java.sql.Timestamp;

/** Don hang cua User. */
public class Order_24133065 implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_PREPARING = "PREPARING";
    public static final String STATUS_SHIPPING = "SHIPPING";
    public static final String STATUS_DELIVERING = "DELIVERING";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_RETURNED = "RETURNED";
    public static final String PAYMENT_COD = "COD";

    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;
    private String status;
    private String paymentMethod;
    private boolean paid;
    private String recipientName;
    private String phone;
    private String address;
    private String note;
    private String customerEmail;
    private String customerName;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public boolean isTerminal() {
        return STATUS_DELIVERED.equals(status) || STATUS_CANCELLED.equals(status) || STATUS_RETURNED.equals(status);
    }

    public String getOrderDateFormatted() {
        return orderDate == null ? "" : new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(orderDate);
    }

    public String getStatusLabel() {
        return switch (status == null ? "" : status) {
            case STATUS_NEW -> "Đơn hàng mới";
            case STATUS_CONFIRMED -> "Đã xác nhận";
            case STATUS_PREPARING -> "Chuẩn bị hàng";
            case STATUS_SHIPPING -> "Vận chuyển";
            case STATUS_DELIVERING -> "Giao hàng";
            case STATUS_DELIVERED -> "Đã giao";
            case STATUS_CANCELLED -> "Đơn hàng hủy";
            case STATUS_RETURNED -> "Đơn hàng hoàn";
            default -> status;
        };
    }

    public int getStatusStep() {
        return switch (status == null ? "" : status) {
            case STATUS_NEW -> 1;
            case STATUS_CONFIRMED -> 2;
            case STATUS_PREPARING -> 3;
            case STATUS_SHIPPING -> 4;
            case STATUS_DELIVERING -> 5;
            case STATUS_DELIVERED -> 6;
            case STATUS_CANCELLED, STATUS_RETURNED -> 0;
            default -> 0;
        };
    }

    public String getStatusCss() {
        return switch (status == null ? "" : status) {
            case STATUS_NEW, STATUS_PREPARING, STATUS_SHIPPING, STATUS_DELIVERING -> "pending";
            case STATUS_CONFIRMED, STATUS_DELIVERED -> "success";
            case STATUS_CANCELLED, STATUS_RETURNED -> "failed";
            default -> "pending";
        };
    }
}
