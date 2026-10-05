/*
   BookStoreGK - SQL Server
   Bo sung cho User:
   1) Gio hang
   2) Dat hang COD
   3) Lich su don hang + loc 8 trang thai

   Chay script nay trong database BookStore sau khi tao bang users/books.
*/

IF OBJECT_ID('dbo.carts', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.carts (
        cart_id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL UNIQUE,
        CONSTRAINT FK_carts_users FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END;
GO

IF OBJECT_ID('dbo.cart_items', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.cart_items (
        cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
        cart_id INT NOT NULL,
        bookid INT NOT NULL,
        quantity INT NOT NULL,
        CONSTRAINT CK_cart_items_quantity CHECK (quantity > 0),
        CONSTRAINT UQ_cart_items_cart_book UNIQUE (cart_id, bookid),
        CONSTRAINT FK_cart_items_cart FOREIGN KEY (cart_id) REFERENCES dbo.carts(cart_id) ON DELETE CASCADE,
        CONSTRAINT FK_cart_items_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid)
    );
END;
GO

IF OBJECT_ID('dbo.orders', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        order_id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        order_date DATETIME2 NOT NULL CONSTRAINT DF_orders_order_date DEFAULT SYSDATETIME(),
        total_amount DECIMAL(18,2) NOT NULL,
        status VARCHAR(20) NOT NULL CONSTRAINT DF_orders_status DEFAULT 'NEW',
        payment_method VARCHAR(10) NOT NULL CONSTRAINT DF_orders_payment_method DEFAULT 'COD',
        paid BIT NOT NULL CONSTRAINT DF_orders_paid DEFAULT 0,
        recipient_name NVARCHAR(255) NOT NULL,
        phone NVARCHAR(30) NOT NULL,
        address NVARCHAR(500) NOT NULL,
        note NVARCHAR(500) NULL,
        CONSTRAINT CK_orders_status CHECK (status IN ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')),
        CONSTRAINT CK_orders_payment_method CHECK (payment_method = 'COD'),
        CONSTRAINT CK_orders_total CHECK (total_amount >= 0),
        CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END;
GO

IF OBJECT_ID('dbo.order_details', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_details (
        order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
        order_id INT NOT NULL,
        bookid INT NOT NULL,
        product_name NVARCHAR(255) NOT NULL,
        price DECIMAL(18,2) NOT NULL,
        quantity INT NOT NULL,
        CONSTRAINT CK_order_details_quantity CHECK (quantity > 0),
        CONSTRAINT CK_order_details_price CHECK (price >= 0),
        CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id) ON DELETE CASCADE,
        CONSTRAINT FK_order_details_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_orders_user_date' AND object_id = OBJECT_ID('dbo.orders'))
    CREATE INDEX IX_orders_user_date ON dbo.orders(user_id, order_date DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_orders_status' AND object_id = OBJECT_ID('dbo.orders'))
    CREATE INDEX IX_orders_status ON dbo.orders(status);
GO

/*
   ==============================================================
   DEMO: doi trang thai trong database de quan sat lich su User
   Thay @OrderId bang ma don hang cua ban.
   Khong can sua code.
   ==============================================================

   DECLARE @OrderId INT = 1;
   UPDATE dbo.orders SET status = 'NEW'        WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'CONFIRMED' WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'PREPARING' WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'SHIPPING'  WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'DELIVERING' WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'DELIVERED', paid = 1 WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'CANCELLED' WHERE order_id = @OrderId;
   UPDATE dbo.orders SET status = 'RETURNED'  WHERE order_id = @OrderId;

   SELECT order_id, user_id, order_date, total_amount, status, payment_method, paid,
          recipient_name, phone, address
   FROM dbo.orders
   WHERE order_id = @OrderId;
*/
