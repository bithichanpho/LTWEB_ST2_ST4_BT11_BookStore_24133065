package service;

import java.sql.*;
import java.util.*;
import model.OrderDetail_24133065;
import model.Order_24133065;
import repository.CartRepository_24133065;
import repository.DBConnection_24133065;
import repository.OrderRepository_24133065;

/** Dat hang COD + quan ly/truyen trang thai don hang cho Admin. */
public class OrderService_24133065 {
    private static final List<String> ADMIN_FLOW = List.of(
            Order_24133065.STATUS_NEW,
            Order_24133065.STATUS_CONFIRMED,
            Order_24133065.STATUS_PREPARING,
            Order_24133065.STATUS_SHIPPING,
            Order_24133065.STATUS_DELIVERING,
            Order_24133065.STATUS_DELIVERED);

    private static final List<String> VALID_STATUS_LIST = List.of(
            Order_24133065.STATUS_NEW,
            Order_24133065.STATUS_CONFIRMED,
            Order_24133065.STATUS_PREPARING,
            Order_24133065.STATUS_SHIPPING,
            Order_24133065.STATUS_DELIVERING,
            Order_24133065.STATUS_DELIVERED,
            Order_24133065.STATUS_CANCELLED,
            Order_24133065.STATUS_RETURNED);
    private static final Set<String> VALID_STATUSES = Collections.unmodifiableSet(new LinkedHashSet<>(VALID_STATUS_LIST));

    private final OrderRepository_24133065 orderRepository = new OrderRepository_24133065();
    private final CartRepository_24133065 cartRepository = new CartRepository_24133065();

    public Order_24133065 placeCodOrder(int userId, String recipientName, String phone, String address, String note) throws SQLException {
        validate(recipientName, phone, address, note);
        try (Connection con = DBConnection_24133065.getConnection()) {
            con.setAutoCommit(false);
            try {
                List<CartRow> rows = loadCartForUpdate(con, userId);
                if (rows.isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống, không thể đặt hàng.");

                double total = 0;
                for (CartRow r : rows) {
                    if (r.quantity <= 0) throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
                    if (r.quantity > r.stock) throw new IllegalArgumentException("Sách '" + r.title + "' chỉ còn " + r.stock + " cuốn.");
                    total += r.price * r.quantity;
                }

                int orderId;
                String insertOrder = "INSERT INTO orders(user_id, order_date, total_amount, status, payment_method, paid, recipient_name, phone, address, note) "
                        + "VALUES (?, GETDATE(), ?, ?, 'COD', 0, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId); ps.setDouble(2, total); ps.setString(3, Order_24133065.STATUS_NEW);
                    ps.setString(4, recipientName); ps.setString(5, phone); ps.setString(6, address); ps.setString(7, note);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Không tạo được mã đơn hàng.");
                        orderId = rs.getInt(1);
                    }
                }

                String detailSql = "INSERT INTO order_details(order_id, bookid, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
                String stockSql = "UPDATE books SET quantity = quantity - ? WHERE bookid = ? AND quantity >= ?";
                try (PreparedStatement detailPs = con.prepareStatement(detailSql); PreparedStatement stockPs = con.prepareStatement(stockSql)) {
                    for (CartRow r : rows) {
                        detailPs.setInt(1, orderId); detailPs.setInt(2, r.bookid); detailPs.setString(3, r.title); detailPs.setDouble(4, r.price); detailPs.setInt(5, r.quantity); detailPs.addBatch();
                        stockPs.setInt(1, r.quantity); stockPs.setInt(2, r.bookid); stockPs.setInt(3, r.quantity);
                        if (stockPs.executeUpdate() != 1) throw new IllegalArgumentException("Tồn kho vừa thay đổi, vui lòng đặt lại đơn hàng.");
                    }
                    detailPs.executeBatch();
                }
                cartRepository.clearByUser(con, userId);
                con.commit();

                Order_24133065 order = orderRepository.findOwnedById(userId, orderId);
                if (order == null) throw new SQLException("Không đọc lại được đơn hàng vừa tạo.");
                return order;
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException ignored) { }
                if (ex instanceof IllegalArgumentException iae) throw iae;
                if (ex instanceof SQLException se) throw se;
                throw new SQLException(ex);
            } finally {
                try { con.setAutoCommit(true); } catch (SQLException ignored) { }
            }
        }
    }

    public List<Order_24133065> getHistory(int userId, String status) throws SQLException {
        if (!VALID_STATUSES.contains(status)) status = "";
        return orderRepository.findByUser(userId, status);
    }

    public Order_24133065 getOrder(int userId, int orderId) throws SQLException { return orderRepository.findOwnedById(userId, orderId); }
    public List<OrderDetail_24133065> getDetails(int userId, int orderId) throws SQLException { return orderRepository.findDetails(userId, orderId); }

    // ======================== ADMIN ========================
    public List<Order_24133065> getAdminOrders(String status, int page, int pageSize) throws SQLException {
        status = normalizeAdminStatus(status);
        return orderRepository.findAllForAdmin(status, page, pageSize);
    }

    public int getAdminTotalPages(String status, int pageSize) throws SQLException {
        status = normalizeAdminStatus(status);
        int total = orderRepository.countForAdmin(status);
        return Math.max(1, (int) Math.ceil(total / (double) pageSize));
    }

    public Map<String, Integer> getAdminStatusCounts() throws SQLException {
        Map<String, Integer> source = orderRepository.countByStatusesForAdmin();
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String s : VALID_STATUS_LIST) result.put(s, source.getOrDefault(s, 0));
        return result;
    }

    public Order_24133065 getAdminOrder(int orderId) throws SQLException { return orderRepository.findByIdForAdmin(orderId); }
    public List<OrderDetail_24133065> getAdminDetails(int orderId) throws SQLException { return orderRepository.findDetailsForAdmin(orderId); }

    /**
     * Quy trinh Admin:
     * NEW -> CONFIRMED -> PREPARING -> SHIPPING -> DELIVERING -> DELIVERED
     * Nhánh đặc biệt: NEW/CONFIRMED/PREPARING -> CANCELLED; DELIVERED -> RETURNED.
     */
    public Order_24133065 updateAdminStatus(int orderId, String newStatus) throws SQLException {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }

        try (Connection con = DBConnection_24133065.getConnection()) {
            con.setAutoCommit(false);
            try {
                Order_24133065 order = orderRepository.findByIdForAdmin(con, orderId, true);
                if (order == null) throw new IllegalArgumentException("Đơn hàng không tồn tại.");

                String current = order.getStatus();
                if (order.isTerminal() && !Order_24133065.STATUS_DELIVERED.equals(current)) {
                    throw new IllegalStateException("Đơn hàng đã kết thúc với trạng thái '" + order.getStatusLabel() + "', không thể thay đổi.");
                }

                if (Order_24133065.STATUS_CANCELLED.equals(newStatus)) {
                    if (!Set.of(Order_24133065.STATUS_NEW, Order_24133065.STATUS_CONFIRMED, Order_24133065.STATUS_PREPARING).contains(current)) {
                        throw new IllegalStateException("Chỉ có thể hủy đơn ở bước mới, đã xác nhận hoặc chuẩn bị hàng.");
                    }
                    orderRepository.restoreStock(con, orderId);
                    orderRepository.updateStatus(con, orderId, newStatus, false);
                } else if (Order_24133065.STATUS_RETURNED.equals(newStatus)) {
                    if (!Order_24133065.STATUS_DELIVERED.equals(current)) {
                        throw new IllegalStateException("Chỉ có thể chuyển sang 'Đơn hàng hoàn' sau khi đơn đã giao.");
                    }
                    orderRepository.updateStatus(con, orderId, newStatus, order.isPaid());
                } else {
                    int currentIdx = ADMIN_FLOW.indexOf(current);
                    int newIdx = ADMIN_FLOW.indexOf(newStatus);
                    if (currentIdx < 0 || newIdx != currentIdx + 1) {
                        throw new IllegalStateException("Chỉ được chuyển theo đúng quy trình: Đã xác nhận → Chuẩn bị hàng → Vận chuyển → Giao hàng → Đã giao.");
                    }
                    boolean paid = order.isPaid();
                    if (Order_24133065.STATUS_DELIVERED.equals(newStatus) && Order_24133065.PAYMENT_COD.equals(order.getPaymentMethod())) {
                        paid = true;
                    }
                    orderRepository.updateStatus(con, orderId, newStatus, paid);
                }

                con.commit();
                Order_24133065 updated = orderRepository.findByIdForAdmin(orderId);
                return updated;
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException ignored) { }
                if (ex instanceof IllegalArgumentException iae) throw iae;
                if (ex instanceof IllegalStateException ise) throw ise;
                if (ex instanceof SQLException se) throw se;
                throw new SQLException(ex);
            } finally {
                try { con.setAutoCommit(true); } catch (SQLException ignored) { }
            }
        }
    }

    public String normalizeAdminStatus(String status) {
        if (status == null) return "";
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return VALID_STATUSES.contains(normalized) ? normalized : "";
    }

    public String getNextStatus(String current) {
        int idx = ADMIN_FLOW.indexOf(current);
        return (idx >= 0 && idx < ADMIN_FLOW.size() - 1) ? ADMIN_FLOW.get(idx + 1) : null;
    }

    public String getNextStatusLabel(String current) {
        String next = getNextStatus(current);
        if (next == null) return null;
        Order_24133065 temp = new Order_24133065();
        temp.setStatus(next);
        return temp.getStatusLabel();
    }

    private void validate(String name, String phone, String address, String note) {
        if (name == null || name.isBlank() || name.length() > 255) throw new IllegalArgumentException("Họ tên người nhận không hợp lệ.");
        if (phone == null || !phone.matches("[+0-9 ()-]{7,20}")) throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        if (address == null || address.isBlank() || address.length() > 500) throw new IllegalArgumentException("Địa chỉ giao hàng không hợp lệ.");
        if (note != null && note.length() > 500) throw new IllegalArgumentException("Ghi chú tối đa 500 ký tự.");
    }

    private List<CartRow> loadCartForUpdate(Connection con, int userId) throws SQLException {
        String sql = "SELECT ci.bookid, ci.quantity, b.title, b.price, b.quantity AS stock "
                + "FROM cart_items ci WITH (UPDLOCK, ROWLOCK) "
                + "JOIN carts c WITH (UPDLOCK, ROWLOCK) ON c.cart_id = ci.cart_id "
                + "JOIN books b ON b.bookid = ci.bookid "
                + "WHERE c.user_id = ? ORDER BY ci.bookid";
        List<CartRow> rows = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new CartRow(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDouble(4), rs.getInt(5)));
            }
        }
        for (CartRow r : rows) {
            try (PreparedStatement ps = con.prepareStatement("SELECT quantity FROM books WITH (UPDLOCK, ROWLOCK) WHERE bookid = ?")) {
                ps.setInt(1, r.bookid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) r.stock = rs.getInt(1); else throw new IllegalArgumentException("Sách không còn tồn tại: " + r.title);
                }
            }
        }
        return rows;
    }

    private static class CartRow {
        final int bookid, quantity; final String title; final double price; int stock;
        CartRow(int bookid, int quantity, String title, double price, int stock) {
            this.bookid = bookid; this.quantity = quantity; this.title = title; this.price = price; this.stock = stock;
        }
    }
}
