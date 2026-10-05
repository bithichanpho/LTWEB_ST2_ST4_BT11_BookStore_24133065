package repository;

import java.sql.*;
import java.util.*;
import model.Order_24133065;
import model.OrderDetail_24133065;

/** JDBC repository cho don hang cua User va khu vuc Admin. */
public class OrderRepository_24133065 {

    public List<Order_24133065> findByUser(int userId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
              + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
              + "u.email AS customer_email, u.fullname AS customer_name "
              + "FROM orders o LEFT JOIN users u ON u.id = o.user_id WHERE o.user_id = ?");
        if (status != null && !status.isBlank()) sql.append(" AND o.status = ?");
        sql.append(" ORDER BY o.order_date DESC, o.order_id DESC");
        List<Order_24133065> list = new ArrayList<>();
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (status != null && !status.isBlank()) ps.setString(i, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        }
        return list;
    }

    /** Danh sach don hang cho Admin, co phan trang va loc status. */
    public List<Order_24133065> findAllForAdmin(String status, int page, int pageSize) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
              + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
              + "u.email AS customer_email, u.fullname AS customer_name "
              + "FROM orders o LEFT JOIN users u ON u.id = o.user_id");
        if (status != null && !status.isBlank()) sql.append(" WHERE o.status = ?");
        sql.append(" ORDER BY o.order_date DESC, o.order_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        int offset = Math.max(page, 0) * pageSize;
        List<Order_24133065> list = new ArrayList<>();
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            if (status != null && !status.isBlank()) ps.setString(i++, status);
            ps.setInt(i++, offset);
            ps.setInt(i, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public int countForAdmin(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders" + (status != null && !status.isBlank() ? " WHERE status = ?" : "");
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (status != null && !status.isBlank()) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Map<String, Integer> countByStatusesForAdmin() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) counts.put(rs.getString("status"), rs.getInt("total"));
        }
        return counts;
    }

    public Order_24133065 findOwnedById(int userId, int orderId) throws SQLException {
        String sql = "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
                + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
                + "u.email AS customer_email, u.fullname AS customer_name "
                + "FROM orders o LEFT JOIN users u ON u.id = o.user_id "
                + "WHERE o.order_id = ? AND o.user_id = ?";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        }
    }

    public Order_24133065 findByIdForAdmin(int orderId) throws SQLException {
        String sql = "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
                + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
                + "u.email AS customer_email, u.fullname AS customer_name "
                + "FROM orders o LEFT JOIN users u ON u.id = o.user_id WHERE o.order_id = ?";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        }
    }

    public Order_24133065 findByIdForAdmin(Connection con, int orderId, boolean forUpdate) throws SQLException {
        String sql = "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
                + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
                + "u.email AS customer_email, u.fullname AS customer_name "
                + "FROM orders o LEFT JOIN users u ON u.id = o.user_id WHERE o.order_id = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        // SQL Server does not support PostgreSQL's FOR UPDATE; lock via UPDLOCK/HOLDLOCK instead.
        if (forUpdate) {
            sql = "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.status, "
                    + "o.payment_method, o.paid, o.recipient_name, o.phone, o.address, o.note, "
                    + "u.email AS customer_email, u.fullname AS customer_name "
                    + "FROM orders o WITH (UPDLOCK, HOLDLOCK) LEFT JOIN users u ON u.id = o.user_id WHERE o.order_id = ?";
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        }
    }

    public void updateStatus(Connection con, int orderId, String status, boolean paid) throws SQLException {
        String sql = "UPDATE orders SET status = ?, paid = ? WHERE order_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setBoolean(2, paid);
            ps.setInt(3, orderId);
            if (ps.executeUpdate() != 1) throw new SQLException("Không cập nhật được trạng thái đơn hàng.");
        }
    }

    /** Hoan kho cho don bi huy truoc khi giao, trong cung transaction. */
    public void restoreStock(Connection con, int orderId) throws SQLException {
        String sql = "UPDATE b SET b.quantity = b.quantity + d.quantity "
                + "FROM books b JOIN order_details d ON d.bookid = b.bookid "
                + "WHERE d.order_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    public List<OrderDetail_24133065> findDetails(int userId, int orderId) throws SQLException {
        String sql = "SELECT d.order_detail_id, d.order_id, d.bookid, d.product_name, d.price, d.quantity "
                + "FROM order_details d JOIN orders o ON o.order_id = d.order_id "
                + "WHERE o.user_id = ? AND d.order_id = ? ORDER BY d.order_detail_id";
        return queryDetails(sql, userId, orderId, true);
    }

    public List<OrderDetail_24133065> findDetailsForAdmin(int orderId) throws SQLException {
        String sql = "SELECT d.order_detail_id, d.order_id, d.bookid, d.product_name, d.price, d.quantity "
                + "FROM order_details d WHERE d.order_id = ? ORDER BY d.order_detail_id";
        List<OrderDetail_24133065> list = new ArrayList<>();
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapDetail(rs));
            }
        }
        return list;
    }

    private List<OrderDetail_24133065> queryDetails(String sql, int userId, int orderId, boolean owned) throws SQLException {
        List<OrderDetail_24133065> list = new ArrayList<>();
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            if (owned) ps.setInt(i++, userId);
            ps.setInt(i, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapDetail(rs));
            }
        }
        return list;
    }

    private OrderDetail_24133065 mapDetail(ResultSet rs) throws SQLException {
        OrderDetail_24133065 d = new OrderDetail_24133065();
        d.setOrderDetailId(rs.getInt("order_detail_id"));
        d.setOrderId(rs.getInt("order_id"));
        d.setBookid(rs.getInt("bookid"));
        d.setProductName(rs.getString("product_name"));
        d.setPrice(rs.getDouble("price"));
        d.setQuantity(rs.getInt("quantity"));
        return d;
    }

    private Order_24133065 mapOrder(ResultSet rs) throws SQLException {
        Order_24133065 o = new Order_24133065();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaid(rs.getBoolean("paid"));
        o.setRecipientName(rs.getString("recipient_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setNote(rs.getString("note"));
        o.setCustomerEmail(rs.getString("customer_email"));
        o.setCustomerName(rs.getString("customer_name"));
        return o;
    }
}
