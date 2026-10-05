package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Phục vụ ảnh bìa ổn định qua /book-image.
 * Ưu tiên tên cover_image trong DB; nếu file không tồn tại thì fallback b{bookId}.png.
 */
@WebServlet(urlPatterns = {"/book-image"})
public class ImageController_24133065 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int bookId = parsePositiveInt(req.getParameter("bookid"));
        String dbName = safeFileName(req.getParameter("name"));

        Path imageDir = resolveImageDir();
        Path selected = null;

        if (imageDir != null && dbName != null) {
            Path candidate = imageDir.resolve(dbName).normalize();
            if (candidate.startsWith(imageDir.normalize()) && Files.isRegularFile(candidate)) {
                selected = candidate;
            }
        }

        // Dữ liệu cũ có thể lưu tên ảnh không còn tồn tại; dùng ảnh mẫu b1.png, b2.png...
        if (selected == null && imageDir != null && bookId > 0) {
            Path fallback = imageDir.resolve("b" + bookId + ".png").normalize();
            if (fallback.startsWith(imageDir.normalize()) && Files.isRegularFile(fallback)) {
                selected = fallback;
            }
        }

        if (selected == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy ảnh bìa.");
            return;
        }

        writeFile(resp, selected.getFileName().toString(), Files.newInputStream(selected), Files.size(selected));
    }

    private Path resolveImageDir() {
        String realPath = getServletContext().getRealPath("/image");
        return realPath == null ? null : Path.of(realPath).toAbsolutePath().normalize();
    }

    private void writeFile(HttpServletResponse resp, String fileName, InputStream in, long size) throws IOException {
        String type = getServletContext().getMimeType(fileName);
        if (type == null) type = "application/octet-stream";
        resp.setContentType(type);
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setContentLengthLong(size);
        try (InputStream input = in; OutputStream out = resp.getOutputStream()) {
            input.transferTo(out);
        }
    }

    private static String safeFileName(String value) {
        if (value == null || value.isBlank()) return null;
        String name = value.trim().replace('\\', '/');
        int slash = name.lastIndexOf('/');
        if (slash >= 0) name = name.substring(slash + 1);
        if (name.isBlank() || name.contains("..")) return null;
        return name;
    }

    private static int parsePositiveInt(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
