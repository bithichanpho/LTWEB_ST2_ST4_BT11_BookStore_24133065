<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Đặt tiêu đề cho decorator (SiteMesh đọc request scope sau khi render body) --%>
<c:set var="pageTitle" value="Xác minh tài khoản" scope="request" />

<style>
.otp-card{max-width:520px;margin:0 auto}
.otp-icon{width:64px;height:64px;border-radius:50%;background:#e8f1ea;color:#285b40;display:inline-flex;align-items:center;justify-content:center;font-size:1.9rem}
.otp-email{display:inline-block;max-width:100%;padding:4px 12px;border-radius:999px;background:#f1f6f2;color:#285b40;font-weight:700;word-break:break-all}
.otp-boxes{display:flex;justify-content:center;gap:10px;margin:22px 0 6px}
.otp-box{width:52px;height:60px;padding:0;text-align:center;font-size:1.6rem;font-weight:800;color:#285b40;border:1.5px solid #d3e0d7;border-radius:12px;background:#fff;outline:none;transition:border-color .15s,box-shadow .15s}
.otp-box:focus{border-color:#2f6b4f;box-shadow:0 0 0 4px rgba(47,107,79,.15)}
.otp-box.filled{background:#f4f9f5;border-color:#7fae8f}
.otp-hint{text-align:center;font-size:.85rem}
.otp-actions{display:flex;flex-direction:column;gap:12px;margin-top:20px}
.otp-resend{text-align:center;font-size:.92rem}
@media (max-width:420px){.otp-boxes{gap:6px}.otp-box{width:42px;height:52px;font-size:1.35rem;border-radius:10px}}
</style>

<div class="otp-card">
  <div class="card-soft p-4 p-md-5">
    <div class="text-center mb-3">
      <div class="otp-icon"><i class="bi bi-shield-lock-fill"></i></div>
      <h3 class="mt-3" style="color:#285b40;font-weight:800">Xác minh tài khoản</h3>
      <p class="text-muted-green mb-2">Mã OTP gồm 6 chữ số đã được gửi tới email</p>
      <span class="otp-email">${sessionScope.pendingUser.email}</span>
    </div>

    <c:if test="${not empty error}">
      <div class="alert alert-danger mt-3 mb-0"><i class="bi bi-exclamation-triangle-fill me-1"></i> ${error}</div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="alert alert-success mt-3 mb-0"><i class="bi bi-check-circle-fill me-1"></i> ${message}</div>
    </c:if>

    <form action="${ctx}/verify" method="post" id="otpForm" autocomplete="off">
      <input type="hidden" name="otp" id="otpValue">
      <div class="otp-boxes" id="otpBoxes">
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 1" autofocus>
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 2">
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 3">
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 4">
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 5">
        <input class="otp-box" type="text" inputmode="numeric" maxlength="1" aria-label="Chữ số 6">
      </div>
      <p class="otp-hint text-muted-green mb-0"><i class="bi bi-clock"></i> Mã có hiệu lực trong 5 phút</p>

      <div class="otp-actions">
        <button type="submit" class="btn-custom btn-custom-primary w-100" id="otpSubmit" disabled>
          <i class="bi bi-check2-circle"></i> Kích hoạt tài khoản
        </button>
        <div class="otp-resend text-muted-green">
          Chưa nhận được mã? <a href="${ctx}/resend-otp">Gửi lại mã OTP</a>
        </div>
      </div>
    </form>
  </div>
</div>

<script>
(function () {
  var boxes = Array.prototype.slice.call(document.querySelectorAll('#otpBoxes .otp-box'));
  var hidden = document.getElementById('otpValue');
  var submit = document.getElementById('otpSubmit');
  var form = document.getElementById('otpForm');

  function sync() {
    var v = boxes.map(function (b) { return b.value; }).join('');
    hidden.value = v;
    submit.disabled = v.length !== boxes.length;
    boxes.forEach(function (b) { b.classList.toggle('filled', b.value !== ''); });
  }

  boxes.forEach(function (box, i) {
    box.addEventListener('input', function () {
      box.value = box.value.replace(/\D/g, '').slice(-1);
      if (box.value && i < boxes.length - 1) boxes[i + 1].focus();
      sync();
    });
    box.addEventListener('keydown', function (e) {
      if (e.key === 'Backspace' && !box.value && i > 0) { boxes[i - 1].focus(); boxes[i - 1].value = ''; sync(); }
      else if (e.key === 'ArrowLeft' && i > 0) boxes[i - 1].focus();
      else if (e.key === 'ArrowRight' && i < boxes.length - 1) boxes[i + 1].focus();
    });
    box.addEventListener('focus', function () { box.select(); });
    box.addEventListener('paste', function (e) {
      e.preventDefault();
      var text = (e.clipboardData || window.clipboardData).getData('text').replace(/\D/g, '').slice(0, boxes.length);
      text.split('').forEach(function (ch, k) { boxes[k].value = ch; });
      boxes[Math.min(text.length, boxes.length - 1)].focus();
      sync();
    });
  });

  form.addEventListener('submit', function (e) { sync(); if (hidden.value.length !== boxes.length) e.preventDefault(); });
  sync();
})();
</script>