<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="row g-4">
  <div class="col-lg-7"><div class="card-soft p-4"><h5 class="mb-3" style="color:#285b40;font-weight:800;">Thông tin giao hàng</h5><form action="${ctx}/order/place" method="post" accept-charset="UTF-8">
    <div class="mb-3"><label class="form-label">Họ tên người nhận</label><input class="form-control" type="text" name="recipientName" maxlength="255" value="${currentUser.fullname}" required></div>
    <div class="mb-3"><label class="form-label">Số điện thoại</label><input class="form-control" type="text" name="phone" maxlength="20" value="${currentUser.phone}" required></div>
    <div class="mb-3"><label class="form-label">Địa chỉ giao hàng</label><textarea class="form-control" name="address" maxlength="500" rows="3" required></textarea></div>
    <div class="mb-3"><label class="form-label">Ghi chú <span class="text-muted-green">(không bắt buộc)</span></label><textarea class="form-control" name="note" maxlength="500" rows="3"></textarea></div>
    <div class="card border-0 p-3 mb-4" style="background:#eef6f0"><div class="fw-bold mb-1"><i class="bi bi-cash-coin"></i> Thanh toán COD</div><div class="small text-muted-green">Bạn thanh toán khi nhận sách. Chưa thu tiền tại thời điểm đặt hàng.</div></div>
    <button type="submit" class="btn-custom btn-custom-primary w-100"><i class="bi bi-check2-circle"></i> Xác nhận đặt hàng COD</button>
  </form></div></div>
  <div class="col-lg-5"><div class="card-soft p-4"><h5 class="mb-3" style="color:#285b40;font-weight:800;">Tóm tắt đơn hàng</h5><c:forEach items="${cartItems}" var="item"><div class="d-flex justify-content-between gap-3 mb-3"><div><div class="table-product-name">${item.title}</div><div class="small text-muted-green">SL: ${item.quantity}</div></div><div class="table-amount text-nowrap"><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/> đ</div></div></c:forEach><hr><div class="d-flex justify-content-between"><span>Tổng thanh toán</span><strong class="book-price"><fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/> đ</strong></div><a href="${ctx}/cart" class="btn-custom btn-custom-outline-secondary w-100 mt-3"><i class="bi bi-arrow-left"></i> Quay lại giỏ hàng</a></div></div>
</div>
