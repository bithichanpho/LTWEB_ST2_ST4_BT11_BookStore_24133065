<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<c:set var="isEdit" value="${not empty book}" />
<c:set var="actionUrl" value="${isEdit ? '/admin/book/edit' : '/admin/book/add'}" />

<div class="admin-toolbar-row">
    <div><div class="eyebrow">Sách</div><h2 class="content-heading">${isEdit ? 'Cập nhật sách' : 'Thêm sách mới'}</h2><p class="content-description">${isEdit ? 'Chỉnh sửa thông tin và tồn kho của sách.' : 'Nhập đầy đủ thông tin để thêm một đầu sách vào hệ thống.'}</p></div>
    <a class="btn-custom btn-custom-outline-primary" href="${ctx}/admin/books"><i class="bi bi-arrow-left"></i> Quay lại</a>
</div>

<c:if test="${not empty error}"><div class="alert-custom alert-custom-danger"><i class="bi bi-exclamation-triangle-fill alert-custom-icon"></i><div class="alert-custom-content">${error}</div></div></c:if>

<div class="form-card-custom">
    <form action="${pageContext.request.contextPath}${actionUrl}" method="post" enctype="multipart/form-data">
        <c:if test="${isEdit}"><input type="hidden" name="bookid" value="${book.bookid}"></c:if>
        <div class="form-section-grid">
            <div class="form-section-card"><div class="section-heading-small"><i class="bi bi-book"></i><div><h4>Thông tin cơ bản</h4><p>Tên sách, ISBN và tác giả</p></div></div>
                <div class="premium-form-group"><label>Tên sách <span>*</span></label><input class="premium-input" type="text" name="title" value="${book.title}" required placeholder="Ví dụ: Norwegian Wood"></div>
                <div class="premium-form-grid-2"><div class="premium-form-group"><label>Mã ISBN</label><input class="premium-input" type="number" name="isbn" value="${book.isbn}" placeholder="978..."></div><div class="premium-form-group"><label>Publisher</label><input class="premium-input" type="text" name="publisher" value="${book.publisher}" placeholder="Nhà xuất bản"></div></div>
                <div class="premium-form-group"><label>Tác giả <span>*</span></label><div class="author-checks-modern"><c:forEach var="a" items="${authors}"><label><input type="checkbox" name="authorIds" value="${a.authorId}" <c:forEach var="ba" items="${book.authors}"><c:if test="${ba.authorId == a.authorId}">checked</c:if></c:forEach>><span>${a.authorName}</span></label></c:forEach></div></div>
                <div class="premium-form-group"><label>Thêm tác giả mới</label><input class="premium-input" type="text" name="newAuthorName" placeholder="Để trống nếu không thêm"></div>
                <div class="premium-form-group"><label>Mô tả</label><textarea class="premium-input premium-textarea" name="description" placeholder="Mô tả ngắn về sách">${book.description}</textarea></div>
            </div>
            <div class="form-section-card"><div class="section-heading-small"><i class="bi bi-box-seam"></i><div><h4>Giá & tồn kho</h4><p>Thiết lập bán hàng</p></div></div>
                <div class="premium-form-grid-2"><div class="premium-form-group"><label>Giá (VNĐ)</label><input class="premium-input" type="number" step="1000" min="0" name="price" value="${book.price}" required></div><div class="premium-form-group"><label>Số lượng</label><input class="premium-input" type="number" min="0" name="quantity" value="${book.quantity}" required></div></div>
                <div class="premium-form-group"><label>Ngày xuất bản</label><input class="premium-input" type="date" name="publish_date" value="${book.publishDate}"></div>
                <div class="premium-form-group"><label>Ảnh bìa</label><c:if test="${isEdit && not empty book.coverImage}"><div class="current-cover"><img src="${ctx}/book-image?bookid=${book.bookid}&name=${book.coverImage}" alt="Ảnh hiện tại"><div><strong>Ảnh hiện tại</strong><span>${book.coverImage}</span></div></div><input type="hidden" name="oldCoverImage" value="${book.coverImage}"></c:if><input class="premium-input" type="file" name="coverImageFile" accept="image/*"><small class="form-hint">Tối đa 5MB. JPG, PNG, WEBP.</small></div>
                <div class="form-actions"><button type="submit" class="btn-custom btn-custom-primary"><i class="bi bi-check2"></i> ${isEdit ? 'Lưu thay đổi' : 'Thêm sách'}</button><a href="${ctx}/admin/books" class="btn-custom btn-custom-light">Hủy</a></div>
            </div>
        </div>
    </form>
</div>
