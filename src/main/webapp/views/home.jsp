<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="card-soft p-4 mb-4">
  <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
    <div><h4 class="mb-1" style="color:#285b40;font-weight:800;">Khám phá kho sách</h4><div class="text-muted-green">Chọn sách, thêm vào giỏ và thanh toán COD ngay trong vài bước.</div></div>
    <a href="${pageContext.request.contextPath}/books" class="btn-custom btn-custom-outline-primary"><i class="bi bi-grid"></i> Xem tất cả sách</a>
  </div>
</div>

<c:if test="${empty groupedBooks}"><div class="card-soft empty-state"><i class="bi bi-book fs-1 d-block mb-2"></i>Chưa có sách nào trong cửa hàng.</div></c:if>
<c:forEach var="entry" items="${groupedBooks}">
  <div class="mb-4"><div class="d-flex align-items-center justify-content-between mb-3"><h5 class="mb-0" style="color:#315d45;font-weight:800;">Tác giả: ${entry.key}</h5><span class="text-muted-green small">${entry.value.size()} sách</span></div>
    <div class="row g-4">
      <c:forEach var="b" items="${entry.value}">
        <div class="col-sm-6 col-xl-4"><article class="card h-100 border-0 shadow-sm overflow-hidden"><a href="${pageContext.request.contextPath}/book/detail?bookid=${b.bookid}"><img class="book-cover" src="${pageContext.request.contextPath}/book-image?bookid=${b.bookid}&name=${b.coverImage}" alt="${b.title}"></a><div class="p-3"><div class="small text-muted-green mb-1">ISBN ${b.isbn}</div><h5 class="mb-2"><a class="text-decoration-none text-main" href="${pageContext.request.contextPath}/book/detail?bookid=${b.bookid}">${b.title}</a></h5><div class="small text-muted-green mb-2">${b.authorNames}</div><div class="d-flex justify-content-between align-items-center"><span class="book-price"><fmt:formatNumber value="${b.price}" type="number" groupingUsed="true"/> đ</span><span class="book-stock ${b.quantity == 0 ? 'out' : ''}">${b.quantity > 0 ? 'Còn ' : ''}${b.quantity} cuốn</span></div><div class="book-card-actions"><c:choose><c:when test="${b.quantity > 0 and not empty sessionScope.user}"><form action="${pageContext.request.contextPath}/cart/add" method="post"><input type="hidden" name="bookid" value="${b.bookid}"><input type="hidden" name="quantity" value="1"><button class="btn-custom btn-custom-primary w-100" type="submit"><i class="bi bi-cart-plus"></i> Thêm giỏ</button></form></c:when><c:when test="${b.quantity > 0}"><a href="${pageContext.request.contextPath}/login" class="btn-custom btn-custom-primary w-100">Đăng nhập để mua</a></c:when><c:otherwise><button class="btn-custom btn-custom-light w-100" disabled>Hết hàng</button></c:otherwise></c:choose><a href="${pageContext.request.contextPath}/book/detail?bookid=${b.bookid}" class="table-btn-action" title="Xem chi tiết"><i class="bi bi-eye"></i></a></div></div></article></div>
      </c:forEach>
    </div>
  </div>
</c:forEach>

<c:if test="${totalPages > 0}"><div class="d-flex justify-content-center gap-2 mt-4"><c:if test="${currentPage > 1}"><a class="btn-custom btn-custom-outline-secondary btn-custom-sm" href="${pageContext.request.contextPath}/home?page=${currentPage-1}"><i class="bi bi-chevron-left"></i> Trước</a></c:if><span class="btn-custom btn-custom-primary btn-custom-sm">Trang ${currentPage}/${totalPages}</span><c:if test="${currentPage < totalPages}"><a class="btn-custom btn-custom-outline-secondary btn-custom-sm" href="${pageContext.request.contextPath}/home?page=${currentPage+1}">Sau <i class="bi bi-chevron-right"></i></a></c:if></div></c:if>
