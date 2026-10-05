<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="order-detail-topbar">
    <a href="${ctx}/admin/orders" class="btn-custom btn-custom-outline-primary btn-custom-sm"><i class="bi bi-arrow-left"></i> Danh sách đơn</a>
    <div class="d-flex align-items-center gap-2"><span class="status-badge status-${order.statusCss}">${order.statusLabel}</span><span class="payment-mini"><i class="bi bi-cash-coin"></i> COD</span></div>
</div>

<div class="admin-order-detail-grid">
    <div class="order-detail-main">
        <div class="table-card-custom">
            <div class="detail-card-header"><div><span class="eyebrow">Mã đơn</span><h3>#${order.orderId}</h3></div><div class="detail-date"><i class="bi bi-calendar3"></i> ${order.orderDateFormatted}</div></div>
            <div class="table-responsive">
                <table class="table-custom"><thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead><tbody>
                    <c:forEach items="${orderDetails}" var="d"><tr><td><div class="table-user-cell"><div class="order-product-icon"><i class="bi bi-book"></i></div><div><div class="table-user-name">${d.productName}</div><div class="table-user-sub">BookID: ${d.bookid}</div></div></div></td><td><fmt:formatNumber value="${d.price}" type="number" groupingUsed="true"/> đ</td><td>${d.quantity}</td><td class="table-amount"><fmt:formatNumber value="${d.subtotal}" type="number" groupingUsed="true"/> đ</td></tr></c:forEach>
                </tbody></table>
            </div>
            <div class="order-total-row"><span>Tổng tiền đơn hàng</span><strong><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> đ</strong></div>
        </div>

        <div class="table-card-custom p-4">
            <div class="section-heading-small"><i class="bi bi-truck"></i><div><h4>Tiến trình đơn hàng</h4><p>Quy trình xử lý của Admin</p></div></div>
            <div class="order-progress">
                <c:forEach var="step" items="${flowStatuses}" varStatus="loop">
                    <div class="progress-step ${order.statusStep >= loop.index + 1 ? 'done' : ''} ${order.status == step ? 'current' : ''}">
                        <div class="progress-dot"><c:choose><c:when test="${order.statusStep > loop.index + 1}"><i class="bi bi-check"></i></c:when><c:otherwise>${loop.index + 1}</c:otherwise></c:choose></div>
                        <div class="progress-label">
                            <c:choose>
                                <c:when test="${step == 'NEW'}">Đơn mới</c:when>
                                <c:when test="${step == 'CONFIRMED'}">Đã xác nhận</c:when>
                                <c:when test="${step == 'PREPARING'}">Chuẩn bị hàng</c:when>
                                <c:when test="${step == 'SHIPPING'}">Vận chuyển</c:when>
                                <c:when test="${step == 'DELIVERING'}">Giao hàng</c:when>
                                <c:otherwise>Đã giao</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${order.status == 'CANCELLED'}"><div class="special-status-box danger"><i class="bi bi-x-octagon"></i><div><strong>Đơn hàng hủy</strong><span>Đơn đã dừng xử lý và tồn kho đã được hoàn lại.</span></div></div></c:if>
            <c:if test="${order.status == 'RETURNED'}"><div class="special-status-box warning"><i class="bi bi-arrow-return-left"></i><div><strong>Đơn hàng hoàn</strong><span>Khách đã nhận hàng và đơn được chuyển sang trạng thái hoàn.</span></div></div></c:if>
        </div>
    </div>

    <div class="order-detail-side">
        <div class="admin-action-card">
            <div class="action-card-head"><div><span class="eyebrow">Xử lý đơn</span><h3>Trạng thái</h3></div><i class="bi bi-sliders2-vertical"></i></div>
            <div class="current-status-panel"><span class="label">Hiện tại</span><span class="status-badge status-${order.statusCss}">${order.statusLabel}</span></div>

            <c:choose>
                <c:when test="${order.status == 'NEW'}"><form action="${ctx}/admin/order/update-status" method="post"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="CONFIRMED"><button class="btn-custom btn-custom-primary w-100 admin-status-btn" type="submit"><i class="bi bi-check2-circle"></i> Xác nhận đơn hàng</button></form></c:when>
                <c:when test="${order.status == 'CONFIRMED'}"><form action="${ctx}/admin/order/update-status" method="post"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="PREPARING"><button class="btn-custom btn-custom-primary w-100 admin-status-btn" type="submit"><i class="bi bi-box-seam"></i> Chuyển sang chuẩn bị hàng</button></form></c:when>
                <c:when test="${order.status == 'PREPARING'}"><form action="${ctx}/admin/order/update-status" method="post"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="SHIPPING"><button class="btn-custom btn-custom-primary w-100 admin-status-btn" type="submit"><i class="bi bi-truck"></i> Chuyển sang vận chuyển</button></form></c:when>
                <c:when test="${order.status == 'SHIPPING'}"><form action="${ctx}/admin/order/update-status" method="post"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="DELIVERING"><button class="btn-custom btn-custom-primary w-100 admin-status-btn" type="submit"><i class="bi bi-bicycle"></i> Chuyển sang giao hàng</button></form></c:when>
                <c:when test="${order.status == 'DELIVERING'}"><form action="${ctx}/admin/order/update-status" method="post"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="DELIVERED"><button class="btn-custom btn-custom-secondary w-100 admin-status-btn" type="submit"><i class="bi bi-patch-check"></i> Xác nhận đã giao</button></form></c:when>
                <c:when test="${order.status == 'DELIVERED'}"><form action="${ctx}/admin/order/update-status" method="post" onsubmit="return confirm('Xác nhận chuyển đơn này sang Đơn hàng hoàn?');"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="RETURNED"><button class="btn-custom btn-custom-warning w-100 admin-status-btn" type="submit"><i class="bi bi-arrow-return-left"></i> Chuyển sang đơn hoàn</button></form></c:when>
            </c:choose>

            <c:if test="${order.status == 'NEW' || order.status == 'CONFIRMED' || order.status == 'PREPARING'}">
                <form action="${ctx}/admin/order/update-status" method="post" class="mt-2" onsubmit="return confirm('Hủy đơn hàng này? Tồn kho sẽ được hoàn lại.');"><input type="hidden" name="orderId" value="${order.orderId}"><input type="hidden" name="status" value="CANCELLED"><button class="btn-custom btn-custom-outline-danger w-100" type="submit"><i class="bi bi-x-circle"></i> Hủy đơn hàng</button></form>
            </c:if>
            <div class="action-note"><i class="bi bi-info-circle"></i> Chỉ được chuyển đúng một bước theo quy trình. Đơn đã hủy/hoàn không thể quay lại.</div>
        </div>

        <div class="table-card-custom p-4">
            <div class="section-heading-small"><i class="bi bi-person-vcard"></i><div><h4>Thông tin nhận hàng</h4><p>Thông tin khách cung cấp khi checkout</p></div></div>
            <div class="info-list"><div><span>Người nhận</span><strong>${order.recipientName}</strong></div><div><span>Số điện thoại</span><strong>${order.phone}</strong></div><div><span>Email</span><strong>${order.customerEmail}</strong></div><div><span>Địa chỉ</span><strong>${order.address}</strong></div><c:if test="${not empty order.note}"><div><span>Ghi chú</span><strong>${order.note}</strong></div></c:if></div>
        </div>
    </div>
</div>
