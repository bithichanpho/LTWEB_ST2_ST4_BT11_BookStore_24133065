<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="table-card-custom">
  <div class="table-responsive">
    <table class="table-custom align-middle">
      <thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th><th class="text-center">Hành động</th></tr></thead>
      <tbody>
        <c:forEach items="${cartItems}" var="item">
          <tr>
            <td><div class="d-flex align-items-center gap-3"><img class="cart-thumb" src="${ctx}/book-image?bookid=${item.bookid}&name=${item.coverImage}" alt="${item.title}"><div><div class="table-product-name">${item.title}</div><div class="small text-muted-green">Còn ${item.stock} cuốn</div></div></div></td>
            <td class="table-amount"><fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/> đ</td>
            <td><form action="${ctx}/cart/update" method="post" class="qty-form"><input type="hidden" name="cartItemId" value="${item.cartItemId}"><input class="form-control qty-input" type="number" name="quantity" min="1" max="${item.stock}" value="${item.quantity}" required><button class="table-btn-action" type="submit" title="Cập nhật"><i class="bi bi-arrow-repeat"></i></button></form></td>
            <td class="table-amount"><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/> đ</td>
            <td><div class="d-flex justify-content-center"><form action="${ctx}/cart/remove" method="post" onsubmit="return confirm('Xóa sản phẩm này khỏi giỏ hàng?');"><input type="hidden" name="cartItemId" value="${item.cartItemId}"><button class="table-btn-action delete" type="submit" title="Xóa"><i class="bi bi-trash"></i></button></form></div></td>
          </tr>
        </c:forEach>
        <c:if test="${empty cartItems}"><tr><td colspan="5"><div class="empty-state"><i class="bi bi-cart-x fs-1 d-block mb-2"></i>Giỏ hàng đang trống.<div class="mt-3"><a href="${ctx}/books" class="btn-custom btn-custom-primary">Tiếp tục mua sách</a></div></div></td></tr></c:if>
      </tbody>
    </table>
  </div>
  <c:if test="${not empty cartItems}"><div class="d-flex flex-column flex-md-row justify-content-end align-items-md-center gap-3 p-3 border-top"><div style="font-size:1.12rem">Tổng tiền: <strong class="book-price"><fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/> đ</strong></div><a href="${ctx}/checkout" class="btn-custom btn-custom-primary"><i class="bi bi-bag-check"></i> Tiến hành thanh toán</a></div></c:if>
</div>
