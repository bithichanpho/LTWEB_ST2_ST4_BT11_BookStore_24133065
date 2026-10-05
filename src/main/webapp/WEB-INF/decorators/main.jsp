<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="currentUser" value="${sessionScope.user}" />
<c:set var="isAdmin" value="${not empty currentUser and currentUser.admin}" />
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><c:out value="${pageTitle}" default="BOOKSTORE" /> - BOOKSTORE</title>
<link rel="icon" type="image/png" href="${ctx}/assets/images/favicon.ico">
<link rel="stylesheet" href="${ctx}/assets/libs/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/assets/libs/bootstrap-icons/bootstrap-icons.css">
<link rel="stylesheet" href="${ctx}/assets/css/main.css">
<style>
.btn-sidebar-outline{background:transparent;border:1px solid rgba(255,255,255,.5);color:#fff}.btn-sidebar-outline:hover{background:rgba(255,255,255,.12);border-color:#fff;color:#fff}
.book-price{color:var(--brand-forest-medium,#2f6b4f);font-size:1.08rem;font-weight:800}.book-stock{font-size:.82rem;color:#6d846f}.book-stock.out{color:#a94b4b}.book-cover{height:310px;width:100%;object-fit:cover;background:#f5f7f5}.book-card-actions{display:flex;gap:8px;align-items:center;margin-top:14px}.book-card-actions form{flex:1}.status-filter-wrap{display:flex;gap:8px;flex-wrap:wrap}.status-filter{display:inline-flex;align-items:center;gap:6px;padding:8px 12px;border:1px solid #d9e4dc;border-radius:999px;text-decoration:none;color:#52705c;background:#fff;font-size:.86rem}.status-filter.active,.status-filter:hover{background:#e8f1ea;color:#285b40;border-color:#b9d0bf}.order-id{font-weight:800;color:#2f5e45}.timeline{display:grid;gap:10px}.timeline-step{display:flex;gap:12px;align-items:center;padding:11px 13px;border:1px solid #e6eee8;border-radius:10px;background:#fafcfb}.timeline-step.done{background:#eef7f0;border-color:#c7dfcc}.timeline-step.current{box-shadow:0 0 0 2px rgba(47,107,79,.12);border-color:#76a789}.timeline-dot{width:28px;height:28px;border-radius:50%;display:flex;align-items:center;justify-content:center;background:#dce7df;color:#456350;font-size:.82rem;font-weight:700}.timeline-step.done .timeline-dot{background:#2f6b4f;color:#fff}.timeline-step.current .timeline-dot{background:#6f9c7e;color:#fff}.cart-thumb{width:56px;height:70px;object-fit:cover;border-radius:8px;background:#f5f5f5}.qty-form{display:flex;align-items:center;gap:6px}.qty-form .qty-input{width:80px}.empty-state{padding:55px 20px;text-align:center;color:#78907d}.flash-stack{margin:0 24px}.card-soft{border:1px solid #e4ece6;border-radius:14px;background:#fff;box-shadow:0 8px 25px rgba(31,63,45,.05)}
@media (max-width:767.98px){.book-cover{height:250px}.page-header{padding:18px 18px}.page-content{padding-left:18px!important;padding-right:18px!important}.navbar-search-wrapper{display:none!important}}
</style>
<sitemesh:write property='head'/>
</head>
<body>
<div class="sidebar-wrapper" id="sidebar">
  <a href="${ctx}/home" class="sidebar-brand"><i class="bi bi-book-half"></i><span>BOOKSTORE</span></a>
  <div class="flex-grow-1 overflow-y-auto">
    <div class="sidebar-menu-section">
      <div class="sidebar-menu-title">Menu</div>
      <ul class="sidebar-menu-list">
        <li class="sidebar-menu-item"><a href="${ctx}/home" class="sidebar-menu-link ${activeMenu == 'home' ? 'active' : ''}"><i class="bi bi-house-door-fill"></i><span>Trang chủ</span></a></li>
        <li class="sidebar-menu-item"><a href="${ctx}/books" class="sidebar-menu-link ${activeMenu == 'books' ? 'active' : ''}"><i class="bi bi-grid-3x3-gap-fill"></i><span>Tất cả sách</span></a></li>
        <c:if test="${not empty currentUser}">
          <li class="sidebar-menu-item"><a href="${ctx}/cart" class="sidebar-menu-link ${activeMenu == 'cart' ? 'active' : ''}"><i class="bi bi-cart3"></i><span>Giỏ hàng</span><span class="sidebar-menu-badge">${navCartCount}</span></a></li>
          <li class="sidebar-menu-item"><a href="${ctx}/order/history" class="sidebar-menu-link ${activeMenu == 'order-history' ? 'active' : ''}"><i class="bi bi-receipt"></i><span>Đơn hàng của tôi</span></a></li>
        </c:if>
      </ul>
    </div>
    <div class="sidebar-menu-section">
      <div class="sidebar-menu-title">Tài khoản</div>
      <ul class="sidebar-menu-list">
        <c:choose>
          <c:when test="${not empty currentUser}">
            <li class="sidebar-menu-item"><a href="${ctx}/logout" class="sidebar-menu-link"><i class="bi bi-box-arrow-right"></i><span>Đăng xuất</span></a></li>
          </c:when>
          <c:otherwise>
            <li class="sidebar-menu-item"><a href="${ctx}/login" class="sidebar-menu-link"><i class="bi bi-box-arrow-in-right"></i><span>Đăng nhập</span></a></li>
            <li class="sidebar-menu-item"><a href="${ctx}/register" class="sidebar-menu-link"><i class="bi bi-person-plus-fill"></i><span>Đăng ký</span></a></li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
    <c:if test="${isAdmin}">
      <div class="sidebar-menu-section">
        <div class="sidebar-menu-title">Quản trị</div>
        <ul class="sidebar-menu-list">
          <li class="sidebar-menu-item"><a href="${ctx}/admin/books" class="sidebar-menu-link"><i class="bi bi-book"></i><span>Quản lý sách</span></a></li>
          <li class="sidebar-menu-item"><a href="${ctx}/admin/book/add" class="sidebar-menu-link"><i class="bi bi-plus-square"></i><span>Thêm sách</span></a></li>
        </ul>
      </div>
    </c:if>
  </div>
  <c:choose>
    <c:when test="${not empty currentUser}">
      <div class="sidebar-profile">
        <div class="sidebar-profile-img d-flex align-items-center justify-content-center bg-forest-medium text-white fw-bold">${fn:substring(currentUser.fullname,0,1)}</div>
        <div class="sidebar-profile-info"><div class="sidebar-profile-name">${currentUser.fullname}</div><div class="sidebar-profile-email">${currentUser.email}</div></div>
      </div>
    </c:when>
    <c:otherwise><div class="sidebar-profile justify-content-center gap-2"><a href="${ctx}/login" class="btn-custom btn-sidebar-outline btn-custom-sm w-100">Đăng nhập</a><a href="${ctx}/register" class="btn-custom btn-custom-primary btn-custom-sm w-100">Đăng ký</a></div></c:otherwise>
  </c:choose>
</div>
<div class="main-wrapper">
  <header class="navbar-custom">
    <div class="navbar-left"><button class="btn-desktop-toggle d-none d-xl-flex align-items-center justify-content-center me-3" id="desktop-sidebar-toggle" aria-label="Thu gọn"><i class="bi bi-chevron-bar-left"></i></button><button class="sidebar-toggle-btn me-2" id="sidebar-toggle" aria-label="Mở menu"><i class="bi bi-list"></i></button></div>
    <div class="navbar-search-wrapper"><form action="${ctx}/books" method="get" class="w-100 d-flex"><input type="text" class="navbar-search-input" name="keyword" placeholder="Tìm kiếm sách..." value="${keyword}"></form></div>
    <div class="navbar-actions">
      <c:if test="${not empty currentUser}"><a href="${ctx}/cart" class="btn-table-action me-2" title="Giỏ hàng"><i class="bi bi-cart3"></i><span class="ms-1">${navCartCount}</span></a></c:if>
      <c:choose><c:when test="${not empty currentUser}"><div class="dropdown ms-2"><button class="navbar-profile-btn dropdown-toggle" type="button" data-bs-toggle="dropdown"><span class="navbar-profile-name d-none d-md-inline">${currentUser.fullname}</span><c:if test="${isAdmin}"><span class="badge-table success ms-1">admin</span></c:if><i class="bi bi-chevron-down navbar-profile-caret"></i></button><ul class="dropdown-menu dropdown-menu-end dropdown-menu-profile"><li class="dropdown-header">Xin chào!</li><li><a class="dropdown-item" href="${ctx}/order/history"><i class="bi bi-receipt"></i> Đơn hàng của tôi</a></li><li><a class="dropdown-item text-danger" href="${ctx}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li></ul></div></c:when><c:otherwise><a href="${ctx}/login" class="btn-custom btn-custom-outline-primary btn-custom-sm me-2">Đăng nhập</a><a href="${ctx}/register" class="btn-custom btn-custom-primary btn-custom-sm">Đăng ký</a></c:otherwise></c:choose>
    </div>
  </header>
  <div class="flash-stack">
    <c:if test="${not empty sessionScope.flashError}"><div class="alert-custom alert-custom-danger mt-3"><i class="bi bi-exclamation-triangle-fill alert-custom-icon"></i><div class="alert-custom-content">${sessionScope.flashError}</div></div><c:remove var="flashError" scope="session" /></c:if>
    <c:if test="${not empty sessionScope.flashMessage}"><div class="alert-custom alert-custom-success mt-3"><i class="bi bi-check-circle-fill alert-custom-icon"></i><div class="alert-custom-content">${sessionScope.flashMessage}</div></div><c:remove var="flashMessage" scope="session" /></c:if>
  </div>
  <div class="page-header"><div><h1 class="page-title"><c:out value="${pageTitle}" default="BOOKSTORE" /></h1><c:if test="${not empty pageSubtitle}"><p class="page-subtitle">${pageSubtitle}</p></c:if></div><nav aria-label="breadcrumb"><ol class="breadcrumb mb-0"><li class="breadcrumb-item"><a href="${ctx}/home" class="text-decoration-none text-muted-green">Home</a></li><li class="breadcrumb-item active text-main"><c:out value="${pageTitle}" /></li></ol></nav></div>
  <div class="page-content px-4 pb-4"><sitemesh:write property='body'/></div>
  <footer class="footer-custom"><div class="footer-left"><span class="footer-logo"><i class="bi bi-book-half"></i> BOOKSTORE</span><span class="footer-separator">|</span><span class="footer-copy">&copy; 2026 - Lập trình Web</span></div><div>Trần Thị Phương Trang - 24133065 - Mã đề 02</div></footer>
</div>
<script src="${ctx}/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/dashboard.js"></script>
</body>
</html>
