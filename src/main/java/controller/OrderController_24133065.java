package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.Order_24133065;
import model.User_24133065;
import repository.OrderRepository_24133065;
import service.CartService_24133065;
import service.OrderService_24133065;

/** User: checkout COD + lich su + chi tiet don hang. */
@WebServlet(urlPatterns = {"/checkout", "/order/place", "/order/history", "/order/detail"})
public class OrderController_24133065 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CartService_24133065 cartService = new CartService_24133065();
    private final OrderService_24133065 orderService = new OrderService_24133065();
    private static final String[] STATUS_FILTERS = {"", "NEW", "CONFIRMED", "PREPARING", "SHIPPING", "DELIVERING", "DELIVERED", "CANCELLED", "RETURNED"};

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24133065 user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        String path = req.getServletPath();
        try {
            if ("/checkout".equals(path)) {
                var checkoutItems = cartService.getItems(user.getId());
                if (checkoutItems.isEmpty()) { flashError(req, "Giỏ hàng đang trống."); resp.sendRedirect(req.getContextPath() + "/cart"); return; }
                req.setAttribute("cartItems", checkoutItems);
                req.setAttribute("cartTotal", checkoutItems.stream().mapToDouble(i -> i.getSubtotal()).sum());
                req.setAttribute("currentUser", user);
                req.setAttribute("pageTitle", "Thanh toán COD");
                req.setAttribute("pageSubtitle", "Điền thông tin nhận hàng và xác nhận đơn hàng");
                req.setAttribute("activeMenu", "cart");
                req.getRequestDispatcher("/views/checkout.jsp").forward(req, resp);
            } else if ("/order/history".equals(path)) {
                String status = normalizeStatus(req.getParameter("status"));
                req.setAttribute("orderList", orderService.getHistory(user.getId(), status));
                req.setAttribute("selectedStatus", status);
                req.setAttribute("pageTitle", "Đơn hàng của tôi");
                req.setAttribute("pageSubtitle", "Theo dõi lịch sử và trạng thái giao hàng");
                req.setAttribute("activeMenu", "order-history");
                req.getRequestDispatcher("/views/order-history.jsp").forward(req, resp);
            } else if ("/order/detail".equals(path)) {
                int id = parseInt(req.getParameter("id"));
                Order_24133065 order = orderService.getOrder(user.getId(), id);
                if (order == null) { flashError(req, "Đơn hàng không tồn tại hoặc không thuộc tài khoản của bạn."); resp.sendRedirect(req.getContextPath() + "/order/history"); return; }
                req.setAttribute("order", order);
                req.setAttribute("orderDetails", orderService.getDetails(user.getId(), id));
                req.setAttribute("pageTitle", "Đơn hàng #" + id);
                req.setAttribute("pageSubtitle", "Chi tiết đơn hàng và trạng thái hiện tại");
                req.setAttribute("activeMenu", "order-history");
                req.getRequestDispatcher("/views/order-detail.jsp").forward(req, resp);
            }
        } catch (Exception ex) { throw new ServletException("Không thể tải dữ liệu đơn hàng.", ex); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User_24133065 user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if (!"/order/place".equals(req.getServletPath())) { resp.sendError(405); return; }
        try {
            Order_24133065 order = orderService.placeCodOrder(user.getId(), req.getParameter("recipientName"), req.getParameter("phone"), req.getParameter("address"), req.getParameter("note"));
            req.getSession().setAttribute("flashMessage", "Đặt hàng thành công. Mã đơn hàng: #" + order.getOrderId());
            resp.sendRedirect(req.getContextPath() + "/order/detail?id=" + order.getOrderId());
        } catch (IllegalArgumentException ex) { flashError(req, ex.getMessage()); resp.sendRedirect(req.getContextPath() + "/checkout"); }
          catch (Exception ex) { throw new ServletException("Không thể tạo đơn hàng.", ex); }
    }

    private User_24133065 currentUser(HttpServletRequest req) { HttpSession s = req.getSession(false); return s == null ? null : (User_24133065) s.getAttribute("user"); }
    private String normalizeStatus(String s) { if (s == null || s.isBlank()) return ""; for (String v : STATUS_FILTERS) if (v.equals(s)) return s; return ""; }
    private int parseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { throw new IllegalArgumentException("Mã đơn hàng không hợp lệ."); } }
    private void flashError(HttpServletRequest req, String message) { req.getSession().setAttribute("flashError", message); }
}
