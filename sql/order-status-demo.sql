/* Quan sat 8 trang thai don hang cua User */
USE BookStore;
GO
DECLARE @OrderId INT = 1;

-- Chay tung UPDATE mot lan de quan sat UI, sau moi lan F5 trang "Don hang cua toi".
UPDATE dbo.orders SET status = 'NEW'         WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'CONFIRMED' WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'PREPARING' WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'SHIPPING'  WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'DELIVERING' WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'DELIVERED', paid = 1 WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'CANCELLED' WHERE order_id = @OrderId;
-- UPDATE dbo.orders SET status = 'RETURNED'  WHERE order_id = @OrderId;

SELECT order_id, user_id, order_date, total_amount, status, payment_method, paid
FROM dbo.orders
WHERE order_id = @OrderId;
GO
