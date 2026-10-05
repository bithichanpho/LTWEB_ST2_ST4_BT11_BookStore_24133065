package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CartItem_24133065;

/** JDBC repository cho gio hang. */
public class CartRepository_24133065 {

    public List<CartItem_24133065> findItemsByUser(int userId) throws SQLException {
        String sql = "SELECT ci.cart_item_id, c.user_id, ci.bookid, b.title, b.price, b.cover_image, "
                + "ci.quantity, b.quantity AS stock "
                + "FROM cart_items ci INNER JOIN carts c ON c.cart_id = ci.cart_id "
                + "INNER JOIN books b ON b.bookid = ci.bookid "
                + "WHERE c.user_id = ? ORDER BY ci.cart_item_id";
        List<CartItem_24133065> items = new ArrayList<>();
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem_24133065 item = new CartItem_24133065();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setBookid(rs.getInt("bookid"));
                    item.setTitle(rs.getString("title"));
                    item.setPrice(rs.getDouble("price"));
                    item.setCoverImage(rs.getString("cover_image"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setStock(rs.getInt("stock"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public int countQuantityByUser(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(ci.quantity), 0) FROM cart_items ci "
                + "INNER JOIN carts c ON c.cart_id = ci.cart_id WHERE c.user_id = ?";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void addOrIncrement(int userId, int bookid, int quantity) throws SQLException {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        try (Connection con = DBConnection_24133065.getConnection()) {
            con.setAutoCommit(false);
            try {
                int cartId;
                String createCart = "IF NOT EXISTS (SELECT 1 FROM carts WHERE user_id = ?) "
                        + "INSERT INTO carts(user_id) VALUES (?)";
                try (PreparedStatement ps = con.prepareStatement(createCart)) {
                    ps.setInt(1, userId); ps.setInt(2, userId); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("SELECT cart_id FROM carts WITH (UPDLOCK, ROWLOCK) WHERE user_id = ?")) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Không tạo được giỏ hàng.");
                        cartId = rs.getInt(1);
                    }
                }
                int stock;
                try (PreparedStatement ps = con.prepareStatement("SELECT quantity FROM books WITH (UPDLOCK, ROWLOCK) WHERE bookid = ?")) {
                    ps.setInt(1, bookid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Sách không tồn tại.");
                        stock = rs.getInt(1);
                    }
                }
                int current = 0;
                try (PreparedStatement ps = con.prepareStatement("SELECT quantity FROM cart_items WITH (UPDLOCK, ROWLOCK) WHERE cart_id = ? AND bookid = ?")) {
                    ps.setInt(1, cartId); ps.setInt(2, bookid);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) current = rs.getInt(1); }
                }
                if (current + quantity > stock) {
                    throw new IllegalArgumentException("Số lượng vượt tồn kho. Hiện còn " + stock + " cuốn.");
                }
                if (current == 0) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO cart_items(cart_id, bookid, quantity) VALUES (?, ?, ?)")) {
                        ps.setInt(1, cartId); ps.setInt(2, bookid); ps.setInt(3, quantity); ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND bookid = ?")) {
                        ps.setInt(1, current + quantity); ps.setInt(2, cartId); ps.setInt(3, bookid); ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException ignored) { }
                if (ex instanceof IllegalArgumentException iae) throw iae;
                throw ex instanceof SQLException se ? se : new SQLException(ex);
            } finally {
                try { con.setAutoCommit(true); } catch (SQLException ignored) { }
            }
        }
    }

    public void updateQuantity(int userId, int cartItemId, int quantity) throws SQLException {
        try (Connection con = DBConnection_24133065.getConnection()) {
            con.setAutoCommit(false);
            try {
                Integer bookid = null; int stock = 0;
                String sql = "SELECT ci.bookid, b.quantity FROM cart_items ci WITH (UPDLOCK, ROWLOCK) "
                        + "JOIN carts c ON c.cart_id = ci.cart_id JOIN books b WITH (UPDLOCK, ROWLOCK) ON b.bookid = ci.bookid "
                        + "WHERE ci.cart_item_id = ? AND c.user_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, cartItemId); ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Sản phẩm không thuộc giỏ hàng của bạn.");
                        bookid = rs.getInt(1); stock = rs.getInt(2);
                    }
                }
                if (quantity <= 0) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM cart_items WHERE cart_item_id = ?")) {
                        ps.setInt(1, cartItemId); ps.executeUpdate();
                    }
                } else {
                    if (quantity > stock) throw new IllegalArgumentException("Số lượng vượt tồn kho. Hiện còn " + stock + " cuốn.");
                    try (PreparedStatement ps = con.prepareStatement("UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?")) {
                        ps.setInt(1, quantity); ps.setInt(2, cartItemId); ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException ignored) { }
                if (ex instanceof IllegalArgumentException iae) throw iae;
                throw ex instanceof SQLException se ? se : new SQLException(ex);
            } finally { try { con.setAutoCommit(true); } catch (SQLException ignored) { } }
        }
    }

    public void remove(int userId, int cartItemId) throws SQLException {
        String sql = "DELETE ci FROM cart_items ci JOIN carts c ON c.cart_id = ci.cart_id WHERE ci.cart_item_id = ? AND c.user_id = ?";
        try (Connection con = DBConnection_24133065.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartItemId); ps.setInt(2, userId);
            if (ps.executeUpdate() == 0) throw new IllegalArgumentException("Sản phẩm không thuộc giỏ hàng của bạn.");
        }
    }

    public void clearByUser(Connection con, int userId) throws SQLException {
        String sql = "DELETE ci FROM cart_items ci JOIN carts c ON c.cart_id = ci.cart_id WHERE c.user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }
}
