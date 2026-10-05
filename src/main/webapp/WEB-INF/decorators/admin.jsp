<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="active" value="${empty activeMenu ? 'admin-books' : activeMenu}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title><c:out value="${pageTitle}" default="Quản trị - BOOKSTORE" /> - BOOKSTORE</title>
    <link rel="icon" href="${ctx}/assets/images/favicon.ico">
    <link rel="stylesheet" href="${ctx}/assets/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/assets/libs/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${ctx}/assets/css/main.css">
    <sitemesh:write property="head"/>
</head>
<body>
<div class="sidebar-wrapper" id="sidebar">
    <a href="${ctx}/admin/orders" class="sidebar-brand"><i class="bi bi-book-half"></i><span>BOOKSTORE</span></a>
    <div class="flex-grow-1 overflow-y-auto">
        <div class="sidebar-menu-section">
            <div class="sidebar-menu-title">Quản trị</div>
            <ul class="sidebar-menu-list">
                <li class="sidebar-menu-item">
                    <a class="sidebar-menu-link ${active == 'admin-orders' ? 'active' : ''}" href="${ctx}/admin/orders">
                        <i class="bi bi-receipt-cutoff"></i><span>Quản lý đơn hàng</span>
                    </a>
                </li>
                <li class="sidebar-menu-item">
                    <a class="sidebar-menu-link ${active == 'admin-books' ? 'active' : ''}" href="${ctx}/admin/books">
                        <i class="bi bi-book"></i><span>Quản lý sách</span>
                    </a>
                </li>
                <li class="sidebar-menu-item">
                    <a class="sidebar-menu-link ${active == 'admin-book-add' ? 'active' : ''}" href="${ctx}/admin/book/add">
                        <i class="bi bi-plus-square"></i><span>Thêm sách</span>
                    </a>
                </li>
            </ul>
        </div>
        <div class="sidebar-menu-section">
            <div class="sidebar-menu-title">Điều hướng</div>
            <ul class="sidebar-menu-list">
                <li class="sidebar-menu-item"><a class="sidebar-menu-link" href="${ctx}/home"><i class="bi bi-house-door"></i><span>Về trang User</span></a></li>
            </ul>
        </div>
    </div>
    <div class="sidebar-profile">
        <div class="sidebar-profile-img d-flex align-items-center justify-content-center bg-forest-medium text-white fw-bold">
            <c:choose><c:when test="${not empty sessionScope.user.fullname}">${fn:substring(sessionScope.user.fullname, 0, 1)}</c:when><c:otherwise>A</c:otherwise></c:choose>
        </div>
        <div class="sidebar-profile-info">
            <div class="sidebar-profile-name">${sessionScope.user.fullname}</div>
            <div class="sidebar-profile-email">${sessionScope.user.email}</div>
        </div>
        <a class="btn-table-action" href="${ctx}/logout" title="Đăng xuất"><i class="bi bi-box-arrow-right"></i></a>
    </div>
</div>

<div class="main-wrapper">
    <header class="navbar-custom">
        <div class="navbar-left">
            <button class="btn-desktop-toggle d-none d-xl-flex align-items-center justify-content-center me-3" id="desktop-sidebar-toggle" aria-label="Thu gọn menu"><i class="bi bi-chevron-bar-left"></i></button>
            <button class="sidebar-toggle-btn me-2" id="sidebar-toggle" aria-label="Mở menu"><i class="bi bi-list"></i></button>
        </div>
        <div class="navbar-search-wrapper"></div>
        <div class="navbar-actions">
            <span class="badge-table success"><i class="bi bi-shield-check"></i> ADMIN</span>
            <a class="btn-custom btn-custom-outline-primary btn-custom-sm ms-2" href="${ctx}/home"><i class="bi bi-shop"></i> Trang User</a>
        </div>
    </header>

    <div class="flash-stack">
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert-custom alert-custom-danger mt-3"><i class="bi bi-exclamation-triangle-fill alert-custom-icon"></i><div class="alert-custom-content">${sessionScope.flashError}</div></div>
            <c:remove var="flashError" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert-custom alert-custom-success mt-3"><i class="bi bi-check-circle-fill alert-custom-icon"></i><div class="alert-custom-content">${sessionScope.flashMessage}</div></div>
            <c:remove var="flashMessage" scope="session" />
        </c:if>
    </div>

    <div class="page-header">
        <div>
            <h1 class="page-title"><c:out value="${pageTitle}" default="Quản trị" /></h1>
            <c:if test="${not empty pageSubtitle}"><p class="page-subtitle">${pageSubtitle}</p></c:if>
        </div>
    </div>

    <div class="page-content px-4 pb-4">
        <sitemesh:write property="body"/>
    </div>

    <footer class="footer-custom">
        <div class="footer-left"><span class="footer-logo"><i class="bi bi-book-half"></i> BOOKSTORE</span><span class="footer-separator">|</span><span class="footer-copy">&copy; 2026 - Lập trình Web</span></div>
        <div>Trần Thị Phương Trang - 24133065 - Mã đề 02</div>
    </footer>
</div>
<script src="${ctx}/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/dashboard.js"></script>
</body>
</html>
