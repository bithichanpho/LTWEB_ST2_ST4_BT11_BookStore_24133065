<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="admin-stat-grid">
    <div class="admin-stat-card"><div class="admin-stat-icon"><i class="bi bi-receipt"></i></div><div><div class="admin-stat-label">Tất cả đơn</div><div class="admin-stat-value">${totalOrderCount}</div></div></div>
    <div class="admin-stat-card"><div class="admin-stat-icon lime"><i class="bi bi-hourglass-split"></i></div><div><div class="admin-stat-label">Đơn mới</div><div class="admin-stat-value">${statusCounts.NEW}</div></div></div>
    <div class="admin-stat-card"><div class="admin-stat-icon blue"><i class="bi bi-check2-circle"></i></div><div><div class="admin-stat-label">Đang xử lý</div><div class="admin-stat-value">${statusCounts.CONFIRMED + statusCounts.PREPARING + statusCounts.SHIPPING + statusCounts.DELIVERING}</div></div></div>
    <div class="admin-stat-card"><div class="admin-stat-icon green"><i class="bi bi-box2-heart"></i></div><div><div class="admin-stat-label">Đã giao</div><div class="admin-stat-value">${statusCounts.DELIVERED}</div></div></div>
</div>

<div class="table-card-custom admin-order-card">
    <div class="table-header-control admin-order-header">
        <div>
            <h3 class="table-section-title mb-1">Danh sách đơn hàng</h3>
            <p class="table-section-subtitle mb-0">Chọn một đơn để xác nhận và chuyển trạng thái.</p>
        </div>
        <div class="table-search-box admin-order-search">
            <i class="bi bi-search table-search-icon"></i>
            <input type="text" class="table-search-input" id="orderSearch" placeholder="Tìm mã đơn, khách hàng, SĐT...">
        </div>
    </div>

    <div class="status-filter-wrap">
        <a href="${ctx}/admin/orders" class="status-filter ${empty status ? 'active' : ''}"><i class="bi bi-grid"></i> Tất cả <span>${totalOrderCount}</span></a>
        <a href="${ctx}/admin/orders?status=NEW" class="status-filter ${status == 'NEW' ? 'active' : ''}"><i class="bi bi-stars"></i> Mới <span>${statusCounts.NEW}</span></a>
        <a href="${ctx}/admin/orders?status=CONFIRMED" class="status-filter ${status == 'CONFIRMED' ? 'active' : ''}"><i class="bi bi-check-circle"></i> Đã xác nhận <span>${statusCounts.CONFIRMED}</span></a>
        <a href="${ctx}/admin/orders?status=PREPARING" class="status-filter ${status == 'PREPARING' ? 'active' : ''}"><i class="bi bi-box-seam"></i> Chuẩn bị <span>${statusCounts.PREPARING}</span></a>
        <a href="${ctx}/admin/orders?status=SHIPPING" class="status-filter ${status == 'SHIPPING' ? 'active' : ''}"><i class="bi bi-truck"></i> Vận chuyển <span>${statusCounts.SHIPPING}</span></a>
        <a href="${ctx}/admin/orders?status=DELIVERING" class="status-filter ${status == 'DELIVERING' ? 'active' : ''}"><i class="bi bi-bicycle"></i> Giao hàng <span>${statusCounts.DELIVERING}</span></a>
        <a href="${ctx}/admin/orders?status=DELIVERED" class="status-filter ${status == 'DELIVERED' ? 'active' : ''}"><i class="bi bi-patch-check"></i> Đã giao <span>${statusCounts.DELIVERED}</span></a>
        <a href="${ctx}/admin/orders?status=CANCELLED" class="status-filter danger ${status == 'CANCELLED' ? 'active' : ''}"><i class="bi bi-x-circle"></i> Đã hủy <span>${statusCounts.CANCELLED}</span></a>
        <a href="${ctx}/admin/orders?status=RETURNED" class="status-filter warning ${status == 'RETURNED' ? 'active' : ''}"><i class="bi bi-arrow-return-left"></i> Đơn hoàn <span>${statusCounts.RETURNED}</span></a>
    </div>

    <div class="table-responsive">
        <table class="table-custom admin-orders-table" id="orderTable">
            <thead>
                <tr>
                    <th>Mã đơn</th><th>Khách hàng</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th><th class="text-center">Xử lý</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${orderList}" var="o">
                    <tr>
                        <td><a class="table-order-id" href="${ctx}/admin/order/detail?id=${o.orderId}">#${o.orderId}</a></td>
                        <td>
                            <div class="table-user-cell">
                                <div class="table-user-avatar order-avatar"><i class="bi bi-person"></i></div>
                                <div><div class="table-user-name">${o.recipientName}</div><div class="table-user-sub">${o.phone}</div></div>
                            </div>
                        </td>
                        <td>${o.orderDateFormatted}</td>
                        <td class="table-amount"><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" /> đ</td>
                        <td><span class="badge-table ${o.paid ? 'success' : 'pending'}">${o.paid ? 'Đã thu' : 'COD - Chưa thu'}</span></td>
                        <td><span class="status-badge status-${o.statusCss}">${o.statusLabel}</span></td>
                        <td class="text-center">
                            <a href="${ctx}/admin/order/detail?id=${o.orderId}" class="table-btn-action admin-action-btn" title="Xử lý đơn #${o.orderId}"><i class="bi bi-arrow-right"></i></a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orderList}">
                    <tr><td colspan="7"><div class="empty-state"><div class="empty-state-icon"><i class="bi bi-inbox"></i></div><div class="empty-state-title">Không có đơn hàng</div><div class="empty-state-text">Chưa có đơn hàng phù hợp với bộ lọc hiện tại.</div></div></td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<c:if test="${totalPages > 1}">
    <nav class="mt-4"><ul class="pagination justify-content-center">
        <c:forEach begin="1" end="${totalPages}" var="i"><li class="page-item ${i == currentPage ? 'active' : ''}"><a class="page-link" href="${ctx}/admin/orders?page=${i}&status=${status}">${i}</a></li></c:forEach>
    </ul></nav>
</c:if>

<script>
(function(){
  var input=document.getElementById('orderSearch');
  if(!input)return;
  input.addEventListener('input',function(){
    var q=this.value.toLowerCase().trim();
    document.querySelectorAll('#orderTable tbody tr').forEach(function(row){
      row.style.display=!q || row.textContent.toLowerCase().indexOf(q)>=0?'':'none';
    });
  });
})();
</script>
