package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User_24133065;
import service.CartService_24133065;

/** User: xem/sua/xoa/them san pham trong gio hang. */
@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove"})
public class CartController_24133065 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CartService_24133065 cartService = new CartService_24133065();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24133065 user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        try {
            req.setAttribute("cartItems", cartService.getItems(user.getId()));
            req.setAttribute("cartTotal", cartService.calculateTotal(user.getId()));
            req.setAttribute("pageTitle", "Giỏ hàng");
            req.setAttribute("pageSubtitle", "Kiểm tra và cập nhật sản phẩm trước khi đặt hàng");
            req.setAttribute("activeMenu", "cart");
            req.getRequestDispatcher("/views/cart.jsp").forward(req, resp);
        } catch (Exception ex) { throw new ServletException("Không thể tải giỏ hàng.", ex); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User_24133065 user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String path = req.getServletPath();
        try {
            if ("/cart/add".equals(path)) {
                int bookid = parseInt(req.getParameter("bookid"));
                int qty = parseIntDefault(req.getParameter("quantity"), 1);
                cartService.add(user.getId(), bookid, qty);
                req.getSession().setAttribute("flashMessage", "Đã thêm sách vào giỏ hàng.");
            } else if ("/cart/update".equals(path)) {
                cartService.update(user.getId(), parseInt(req.getParameter("cartItemId")), parseInt(req.getParameter("quantity")));
                req.getSession().setAttribute("flashMessage", "Đã cập nhật số lượng.");
            } else if ("/cart/remove".equals(path)) {
                cartService.remove(user.getId(), parseInt(req.getParameter("cartItemId")));
                req.getSession().setAttribute("flashMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
            }
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
        } catch (Exception ex) { throw new ServletException("Không thể xử lý giỏ hàng.", ex); }
        String redirect = req.getParameter("redirect");
        if ("/cart/add".equals(path) && redirect != null && redirect.matches("^/book/detail\\?bookid=\\d+$")) resp.sendRedirect(req.getContextPath() + redirect);
        else if ("/cart/add".equals(path)) resp.sendRedirect(req.getContextPath() + "/cart");
        else resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private User_24133065 currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User_24133065) s.getAttribute("user"); }
    private int parseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { throw new IllegalArgumentException("Dữ liệu không hợp lệ."); } }
    private int parseIntDefault(String s, int fallback) { if (s == null || s.isBlank()) return fallback; return parseInt(s); }
}
