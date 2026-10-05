<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="row g-4">
  <div class="col-lg-8"><div class="table-card-custom"><div class="table-responsive"><table class="table-custom"><thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead><tbody><c:forEach items="${orderDetails}" var="d"><tr><td class="table-product-name">${d.productName}</td><td class="table-amount"><fmt:formatNumber value="${d.price}" type="number" groupingUsed="true"/> đ</td><td>${d.quantity}</td><td class="table-amount"><fmt:formatNumber value="${d.subtotal}" type="number" groupingUsed="true"/> đ</td></tr></c:forEach></tbody></table></div><div class="d-flex justify-content-end p-3 border-top" style="font-size:1.08rem">Tổng đơn hàng: <strong class="book-price ms-1"><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> đ</strong></div></div></div>
  <div class="col-lg-4"><div class="card-soft p-4"><div class="d-flex justify-content-between align-items-start gap-2"><div><h5 class="mb-1" style="color:#285b40;font-weight:800">Đơn hàng #${order.orderId}</h5><div class="small text-muted-green">${order.orderDateFormatted}</div></div><span class="badge-table ${order.statusCss}">${order.statusLabel}</span></div><hr>
    <h6 style="color:#285b40;font-weight:800">Tiến trình</h6><div class="timeline mb-4">
      <div class="timeline-step ${order.statusStep >= 1 ? 'done' : ''} ${order.status == 'NEW' ? 'current' : ''}"><span class="timeline-dot">1</span><div><div class="fw-bold">Đơn hàng mới</div><div class="small text-muted-green">Hệ thống đã tiếp nhận đơn</div></div></div>
      <div class="timeline-step ${order.statusStep >= 2 ? 'done' : ''} ${order.status == 'CONFIRMED' ? 'current' : ''}"><span class="timeline-dot">2</span><div><div class="fw-bold">Đã xác nhận</div><div class="small text-muted-green">Đơn đã được xác nhận</div></div></div>
      <div class="timeline-step ${order.statusStep >= 3 ? 'done' : ''} ${order.status == 'PREPARING' ? 'current' : ''}"><span class="timeline-dot">3</span><div><div class="fw-bold">Chuẩn bị hàng</div><div class="small text-muted-green">Đang đóng gói sách</div></div></div>
      <div class="timeline-step ${order.statusStep >= 4 ? 'done' : ''} ${order.status == 'SHIPPING' ? 'current' : ''}"><span class="timeline-dot">4</span><div><div class="fw-bold">Vận chuyển</div><div class="small text-muted-green">Đơn đang trên đường</div></div></div>
      <div class="timeline-step ${order.statusStep >= 5 ? 'done' : ''} ${order.status == 'DELIVERING' ? 'current' : ''}"><span class="timeline-dot">5</span><div><div class="fw-bold">Giao hàng</div><div class="small text-muted-green">Shipper đang giao tới bạn</div></div></div>
      <div class="timeline-step ${order.statusStep >= 6 ? 'done' : ''} ${order.status == 'DELIVERED' ? 'current' : ''}"><span class="timeline-dot">6</span><div><div class="fw-bold">Đã giao</div><div class="small text-muted-green">Hoàn tất giao hàng, COD được thu</div></div></div>
      <c:if test="${order.status == 'CANCELLED' or order.status == 'RETURNED'}"><div class="timeline-step current"><span class="timeline-dot">!</span><div><div class="fw-bold">${order.statusLabel}</div><div class="small text-muted-green">Trạng thái kết thúc đặc biệt của đơn hàng</div></div></div></c:if>
    </div>
    <div class="mb-2"><strong>Thanh toán:</strong> <c:choose><c:when test="${order.paid}"><span class="badge-table success">Đã thanh toán</span></c:when><c:otherwise><span class="badge-table pending">COD - Chưa thu tiền</span></c:otherwise></c:choose></div>
    <div class="mt-3"><div class="mb-1"><strong>Người nhận:</strong> ${order.recipientName}</div><div class="mb-1"><strong>SĐT:</strong> ${order.phone}</div><div class="mb-1"><strong>Địa chỉ:</strong> ${order.address}</div><c:if test="${not empty order.note}"><div class="mb-1"><strong>Ghi chú:</strong> ${order.note}</div></c:if></div>
    <a href="${ctx}/order/history" class="btn-custom btn-custom-outline-primary w-100 mt-4"><i class="bi bi-arrow-left"></i> Quay lại lịch sử</a>
  </div></div>
</div>
