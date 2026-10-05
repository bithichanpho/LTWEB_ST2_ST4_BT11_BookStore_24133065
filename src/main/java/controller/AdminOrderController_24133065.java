package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Order_24133065;
import service.OrderService_24133065;

/** Admin: quản lý đơn hàng, lọc và chuyển trạng thái theo đúng quy trình. */
@WebServlet(urlPatterns = {"/admin/orders", "/admin/order/detail", "/admin/order/update-status"})
public class AdminOrderController_24133065 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 10;
    private final OrderService_24133065 orderService = new OrderService_24133065();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        req.setCharacterEncoding("UTF-8");
        try {
            if ("/admin/orders".equals(path)) {
                int page = parsePage(req.getParameter("page"));
                String status = orderService.normalizeAdminStatus(req.getParameter("status"));
                int totalPages = orderService.getAdminTotalPages(status, PAGE_SIZE);
                if (page > totalPages) page = totalPages;

                List<Order_24133065> orders = orderService.getAdminOrders(status, page - 1, PAGE_SIZE);
                Map<String, Integer> counts = orderService.getAdminStatusCounts();

                req.setAttribute("orderList", orders);
                req.setAttribute("currentPage", page);
                req.setAttribute("totalPages", totalPages);
                req.setAttribute("status", status);
                req.setAttribute("statusCounts", counts);
                req.setAttribute("totalOrderCount", counts.values().stream().mapToInt(Integer::intValue).sum());
                req.setAttribute("pageTitle", "Quản lý đơn hàng");
                req.setAttribute("pageSubtitle", "Theo dõi, xác nhận và chuyển trạng thái đơn hàng của khách hàng");
                req.setAttribute("activeMenu", "admin-orders");
                req.getRequestDispatcher("/views/admin/orders.jsp").forward(req, resp);
                return;
            }

            if ("/admin/order/detail".equals(path)) {
                int orderId = parseId(req.getParameter("id"));
                Order_24133065 order = orderService.getAdminOrder(orderId);
                if (order == null) {
                    req.getSession().setAttribute("flashError", "Đơn hàng không tồn tại.");
                    resp.sendRedirect(req.getContextPath() + "/admin/orders");
                    return;
                }
                req.setAttribute("order", order);
                req.setAttribute("orderDetails", orderService.getAdminDetails(orderId));
                req.setAttribute("nextStatus", orderService.getNextStatus(order.getStatus()));
                req.setAttribute("nextStatusLabel", orderService.getNextStatusLabel(order.getStatus()));
                req.setAttribute("flowStatuses", List.of(
                        Order_24133065.STATUS_NEW, Order_24133065.STATUS_CONFIRMED, Order_24133065.STATUS_PREPARING,
                        Order_24133065.STATUS_SHIPPING, Order_24133065.STATUS_DELIVERING, Order_24133065.STATUS_DELIVERED));
                req.setAttribute("pageTitle", "Chi tiết đơn #" + orderId);
                req.setAttribute("pageSubtitle", "Kiểm tra thông tin nhận hàng và xử lý trạng thái");
                req.setAttribute("activeMenu", "admin-orders");
                req.getRequestDispatcher("/views/admin/order-detail.jsp").forward(req, resp);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (Exception ex) {
            throw new ServletException("Không thể tải dữ liệu quản lý đơn hàng.", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!"/admin/order/update-status".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        int orderId = parseId(req.getParameter("orderId"));
        String status = req.getParameter("status");
        try {
            Order_24133065 updated = orderService.updateAdminStatus(orderId, status);
            req.getSession().setAttribute("flashMessage",
                    "Đã chuyển đơn #" + orderId + " sang trạng thái: " + updated.getStatusLabel() + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            req.getSession().setAttribute("flashError", ex.getMessage());
        } catch (Exception ex) {
            throw new ServletException("Không thể cập nhật trạng thái đơn hàng.", ex);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/order/detail?id=" + orderId);
    }

    private int parsePage(String raw) {
        try { return Math.max(1, Integer.parseInt(raw)); } catch (Exception e) { return 1; }
    }

    private int parseId(String raw) {
        try {
            int id = Integer.parseInt(raw);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (Exception e) {
            throw new IllegalArgumentException("Mã đơn hàng không hợp lệ.");
        }
    }
}
