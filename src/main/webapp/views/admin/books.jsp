<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="admin-toolbar-row">
    <div><div class="eyebrow">Danh mục</div><h2 class="content-heading">Sách đang quản lý</h2><p class="content-description">Thêm, chỉnh sửa và kiểm soát tồn kho sách trong BookStore.</p></div>
    <a class="btn-custom btn-custom-primary" href="${ctx}/admin/book/add"><i class="bi bi-plus-lg"></i> Thêm sách mới</a>
</div>

<c:if test="${param.deleted == '1'}"><div class="alert-custom alert-custom-success"><i class="bi bi-check-circle-fill alert-custom-icon"></i><div class="alert-custom-content">Đã xóa sách thành công.</div></div></c:if>
<c:if test="${param.deleted == '0'}"><div class="alert-custom alert-custom-danger"><i class="bi bi-exclamation-triangle-fill alert-custom-icon"></i><div class="alert-custom-content">Không thể xóa sách đã xuất hiện trong giỏ hàng hoặc lịch sử đơn hàng.</div></div></c:if>
<c:if test="${param.saved == '1'}"><div class="alert-custom alert-custom-success"><i class="bi bi-check-circle-fill alert-custom-icon"></i><div class="alert-custom-content">Lưu thông tin sách thành công.</div></div></c:if>

<div class="table-card-custom">
    <div class="table-header-control"><div><h3 class="table-section-title mb-1">Danh sách sách</h3><p class="table-section-subtitle mb-0">Hiển thị ${books.size()} sách trên trang hiện tại.</p></div><a href="${ctx}/admin/book/add" class="btn-custom btn-custom-outline-primary btn-custom-sm"><i class="bi bi-plus"></i> Thêm mới</a></div>
    <div class="table-responsive">
        <table class="table-custom admin-books-table">
            <thead><tr><th>Sách</th><th>ISBN</th><th>Tác giả</th><th>Publisher</th><th>Giá</th><th>Ngày xuất bản</th><th>Tồn kho</th><th class="text-center">Thao tác</th></tr></thead>
            <tbody>
                <c:forEach var="b" items="${books}">
                    <tr>
                        <td><div class="table-user-cell"><img class="admin-book-cover" src="${ctx}/book-image?bookid=${b.bookid}&name=${b.coverImage}" alt="${b.title}"><div><div class="table-user-name">${b.title}</div><div class="table-user-sub">BookID #${b.bookid}</div></div></div></td>
                        <td>${b.isbn}</td><td>${b.authorNames}</td><td>${b.publisher}</td><td class="table-amount"><fmt:formatNumber value="${b.price}" type="number" groupingUsed="true"/> đ</td><td>${b.publishDate}</td>
                        <td><span class="stock-badge ${b.quantity <= 5 ? 'low' : ''}">${b.quantity} cuốn</span></td>
                        <td class="text-center"><div class="d-flex justify-content-center gap-2"><a class="table-btn-action" href="${ctx}/admin/book/edit?bookid=${b.bookid}" title="Sửa"><i class="bi bi-pencil"></i></a><form action="${ctx}/admin/book/delete" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn xóa sách này?');"><input type="hidden" name="bookid" value="${b.bookid}"><button type="submit" class="table-btn-action danger" title="Xóa"><i class="bi bi-trash"></i></button></form></div></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty books}"><tr><td colspan="8"><div class="empty-state"><div class="empty-state-icon"><i class="bi bi-book"></i></div><div class="empty-state-title">Chưa có sách</div><div class="empty-state-text">Hãy thêm sách đầu tiên vào kho.</div></div></td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<c:if test="${totalPages > 1}"><nav class="mt-4"><ul class="pagination justify-content-center"><c:forEach var="i" begin="1" end="${totalPages}"><li class="page-item ${i == currentPage ? 'active' : ''}"><a class="page-link" href="${ctx}/admin/books?page=${i}">${i}</a></li></c:forEach></ul></nav></c:if>
