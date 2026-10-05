package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.User_24133065;
import service.CartService_24133065;

/** Cung cap badge so luong san pham trong gio hang cho giao dien chung. */
@WebFilter(urlPatterns = {"/*"})
public class CartCountFilter_24133065 implements Filter {
    private final CartService_24133065 cartService = new CartService_24133065();
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        boolean staticResource = uri.startsWith(ctx + "/assets/") || uri.startsWith(ctx + "/image/") || uri.startsWith(ctx + "/css/") || uri.startsWith(ctx + "/js/") || uri.startsWith(ctx + "/book-image") || uri.endsWith(".ico");
        if (!staticResource) {
            HttpSession s = req.getSession(false);
            User_24133065 user = s == null ? null : (User_24133065) s.getAttribute("user");
            if (user != null) {
                try { req.setAttribute("navCartCount", cartService.countQuantity(user.getId())); }
                catch (Exception ignored) { req.setAttribute("navCartCount", 0); }
            } else req.setAttribute("navCartCount", 0);
        }
        chain.doFilter(request, response);
    }
}
