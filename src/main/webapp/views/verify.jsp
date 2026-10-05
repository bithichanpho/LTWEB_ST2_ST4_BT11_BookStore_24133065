<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="page-title">Kích hoạt tài khoản</h1>
<p class="page-subtitle">Mã OTP đã được gửi đến email của bạn, có hiệu lực trong 5 phút</p>

<div class="form-box">
    <h2>Nhập mã OTP</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>

    <p class="alert alert-info" style="max-width:100%;">
        Một mã OTP gồm 6 chữ số đã được gửi tới địa chỉ email:
        <b>${sessionScope.pendingUser.email}</b>
    </p>

    <form action="${pageContext.request.contextPath}/verify" method="post">
        <div class="form-group">
            <label>Mã OTP:</label>
            <input type="text" name="otp" maxlength="6" placeholder="______"
                   style="font-size:22px; letter-spacing:8px; text-align:center;" required>
        </div>
        <button type="submit" class="btn btn-blue">Kích hoạt</button>
        <a href="${pageContext.request.contextPath}/resend-otp" style="margin-left:10px; font-size:14px;">
            Gửi lại mã OTP
        </a>
    </form>
</div>
