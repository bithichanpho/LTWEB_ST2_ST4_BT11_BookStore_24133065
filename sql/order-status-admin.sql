/*
 BookStoreGK - Admin quản lý trạng thái đơn hàng
 SQL Server / Tomcat 10.1
 Chạy sau cart-order-schema.sql trong database BookStore.
*/
USE BookStore;
GO

/* 1. Bảo đảm orders có đủ cột đang dùng */
IF COL_LENGTH('dbo.orders', 'payment_method') IS NULL
BEGIN
    ALTER TABLE dbo.orders ADD payment_method VARCHAR(10) NOT NULL CONSTRAINT DF_orders_payment_method_v2 DEFAULT 'COD';
END;
GO

IF COL_LENGTH('dbo.orders', 'paid') IS NULL
BEGIN
    ALTER TABLE dbo.orders ADD paid BIT NOT NULL CONSTRAINT DF_orders_paid_v2 DEFAULT 0;
END;
GO

/* 2. Gỡ check constraint trạng thái cũ trước khi chuẩn hóa dữ liệu */
DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql = @sql + N'ALTER TABLE dbo.orders DROP CONSTRAINT [' + cc.name + N'];' + CHAR(10)
FROM sys.check_constraints cc
WHERE cc.parent_object_id = OBJECT_ID('dbo.orders')
  AND (cc.name LIKE '%status%' OR cc.definition LIKE '%status%');
IF @sql <> N'' EXEC sp_executesql @sql;
GO

/* 3. Chuẩn hóa dữ liệu trạng thái từ phiên bản cũ */
IF COL_LENGTH('dbo.orders', 'status') IS NOT NULL
BEGIN
    UPDATE dbo.orders SET status = 'NEW' WHERE status = 'PENDING';
    UPDATE dbo.orders SET status = 'DELIVERED', paid = CASE WHEN payment_method = 'COD' THEN 1 ELSE paid END WHERE status = 'COMPLETED';
END;
GO

/* 4. Ràng buộc 8 trạng thái chính thức */
IF NOT EXISTS (
    SELECT 1 FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID('dbo.orders') AND name = 'CK_orders_status_admin_v2'
)
BEGIN
    ALTER TABLE dbo.orders ADD CONSTRAINT CK_orders_status_admin_v2 CHECK (
        status IN ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')
    );
END;
GO

/* Demo trực tiếp trong SQL Server: */
-- DECLARE @OrderId INT = 1;
-- UPDATE dbo.orders SET status='CONFIRMED' WHERE order_id=@OrderId;
-- SELECT order_id, status, payment_method, paid FROM dbo.orders WHERE order_id=@OrderId;
GO
