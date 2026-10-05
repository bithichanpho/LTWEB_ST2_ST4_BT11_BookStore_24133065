<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="card-soft p-3 mb-3"><div class="status-filter-wrap">
  <c:set var="filters" value="ALL,NEW,CONFIRMED,PREPARING,SHIPPING,DELIVERING,DELIVERED,CANCELLED,RETURNED" />
  <a class="status-filter ${selectedStatus == '' ? 'active' : ''}" href="${ctx}/order/history">Tất cả</a>
  <a class="status-filter ${selectedStatus == 'NEW' ? 'active' : ''}" href="${ctx}/order/history?status=NEW">Đơn hàng mới</a>
  <a class="status-filter ${selectedStatus == 'CONFIRMED' ? 'active' : ''}" href="${ctx}/order/history?status=CONFIRMED">Đã xác nhận</a>
  <a class="status-filter ${selectedStatus == 'PREPARING' ? 'active' : ''}" href="${ctx}/order/history?status=PREPARING">Chuẩn bị hàng</a>
  <a class="status-filter ${selectedStatus == 'SHIPPING' ? 'active' : ''}" href="${ctx}/order/history?status=SHIPPING">Vận chuyển</a>
  <a class="status-filter ${selectedStatus == 'DELIVERING' ? 'active' : ''}" href="${ctx}/order/history?status=DELIVERING">Giao hàng</a>
  <a class="status-filter ${selectedStatus == 'DELIVERED' ? 'active' : ''}" href="${ctx}/order/history?status=DELIVERED">Đã giao</a>
  <a class="status-filter ${selectedStatus == 'CANCELLED' ? 'active' : ''}" href="${ctx}/order/history?status=CANCELLED">Đơn hàng hủy</a>
  <a class="status-filter ${selectedStatus == 'RETURNED' ? 'active' : ''}" href="${ctx}/order/history?status=RETURNED">Đơn hàng hoàn</a>
</div></div>

<div class="table-card-custom"><div class="table-responsive"><table class="table-custom"><thead><tr><th>Mã đơn</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th><th class="text-center">Chi tiết</th></tr></thead><tbody>
<c:forEach items="${orderList}" var="o"><tr><td class="order-id">#${o.orderId}</td><td>${o.orderDateFormatted}</td><td class="table-amount"><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true"/> đ</td><td><c:choose><c:when test="${o.paid}"><span class="badge-table success">Đã thanh toán</span></c:when><c:otherwise><span class="badge-table pending">Chưa thanh toán (COD)</span></c:otherwise></c:choose></td><td><span class="badge-table ${o.statusCss}">${o.statusLabel}</span></td><td class="text-center"><a class="table-btn-action" href="${ctx}/order/detail?id=${o.orderId}" title="Xem chi tiết"><i class="bi bi-eye"></i></a></td></tr></c:forEach>
<c:if test="${empty orderList}"><tr><td colspan="6"><div class="empty-state"><i class="bi bi-receipt fs-1 d-block mb-2"></i>Không có đơn hàng ở trạng thái này.</div></td></tr></c:if>
</tbody></table></div></div>
