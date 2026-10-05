package service;

import java.sql.SQLException;
import java.util.List;
import model.CartItem_24133065;
import repository.CartRepository_24133065;

/** Nghiep vu gio hang cho User. */
public class CartService_24133065 {
    private final CartRepository_24133065 repository = new CartRepository_24133065();

    public List<CartItem_24133065> getItems(int userId) throws SQLException { return repository.findItemsByUser(userId); }

    public double calculateTotal(int userId) throws SQLException {
        return getItems(userId).stream().mapToDouble(CartItem_24133065::getSubtotal).sum();
    }

    public int countQuantity(int userId) throws SQLException { return repository.countQuantityByUser(userId); }

    public void add(int userId, int bookid, int quantity) throws SQLException { repository.addOrIncrement(userId, bookid, quantity); }

    public void update(int userId, int cartItemId, int quantity) throws SQLException { repository.updateQuantity(userId, cartItemId, quantity); }

    public void remove(int userId, int cartItemId) throws SQLException { repository.remove(userId, cartItemId); }
}
