-- ============================================================
-- Script: LangCenterAssetDB Schema
-- Database: SQL Server
-- Author: Loi (DBA)
-- Description: Tạo CSDL cho hệ thống quản lý tài sản
--              trung tâm ngoại ngữ
-- ============================================================

USE master;
GO

-- Tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'LangCenterAssetDB')
BEGIN
    CREATE DATABASE LangCenterAssetDB;
END
GO

USE LangCenterAssetDB;
GO

-- ============================================================
-- TABLE: Roles
-- ============================================================
IF OBJECT_ID('dbo.Roles', 'U') IS NOT NULL DROP TABLE dbo.Roles;
GO

CREATE TABLE dbo.Roles (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(50)  NOT NULL UNIQUE,
    description NVARCHAR(255) NULL,
    created_at  DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- Seed dữ liệu mặc định
INSERT INTO dbo.Roles (name, description) VALUES
    (N'ADMIN',   N'Quản trị viên hệ thống – toàn quyền'),
    (N'MANAGER', N'Quản lý – duyệt mượn/trả, xem báo cáo'),
    (N'STAFF',   N'Nhân viên – tạo phiếu mượn/trả');
GO

-- ============================================================
-- TABLE: Users
-- ============================================================
IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL DROP TABLE dbo.Users;
GO

CREATE TABLE dbo.Users (
    id           INT IDENTITY(1,1) PRIMARY KEY,
    username     NVARCHAR(100) NOT NULL UNIQUE,
    password     NVARCHAR(255) NOT NULL,          -- BCrypt hash
    full_name    NVARCHAR(150) NOT NULL,
    email        NVARCHAR(150) NULL UNIQUE,
    phone        NVARCHAR(20)  NULL,
    role_id      INT           NOT NULL,
    is_active    BIT           NOT NULL DEFAULT 1,
    created_at   DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at   DATETIME2     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Users_Roles FOREIGN KEY (role_id) REFERENCES dbo.Roles(id)
);
GO

-- Seed admin mặc định (password = "admin123" đã BCrypt)
INSERT INTO dbo.Users (username, password, full_name, email, role_id) VALUES
    (N'admin',
     N'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     N'Administrator',
     N'admin@langcenter.edu.vn',
     1);
GO

-- ============================================================
-- TABLE: Assets
-- ============================================================
IF OBJECT_ID('dbo.Assets', 'U') IS NOT NULL DROP TABLE dbo.Assets;
GO

CREATE TABLE dbo.Assets (
    id                 INT IDENTITY(1,1) PRIMARY KEY,
    asset_code         NVARCHAR(50)   NOT NULL UNIQUE,
    name               NVARCHAR(200)  NOT NULL,
    category           NVARCHAR(100)  NULL,
    description        NVARCHAR(500)  NULL,
    total_quantity     INT            NOT NULL DEFAULT 0,
    available_quantity INT            NOT NULL DEFAULT 0,
    unit               NVARCHAR(50)   NULL,       -- Đơn vị tính: cái, bộ, cuốn…
    location           NVARCHAR(200)  NULL,
    status             NVARCHAR(50)   NOT NULL DEFAULT N'ACTIVE',
    -- ACTIVE | INACTIVE | MAINTENANCE
    created_by         INT            NULL,
    created_at         DATETIME2      NOT NULL DEFAULT GETDATE(),
    updated_at         DATETIME2      NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Assets_Users FOREIGN KEY (created_by) REFERENCES dbo.Users(id),
    CONSTRAINT CHK_Assets_Qty  CHECK (available_quantity >= 0 AND total_quantity >= 0)
);
GO

-- Seed dữ liệu mẫu
INSERT INTO dbo.Assets (asset_code, name, category, total_quantity, available_quantity, unit, location, created_by) VALUES
    (N'TB-001', N'Máy chiếu Epson EB-X51',   N'Thiết bị điện tử', 5,  5,  N'Cái', N'Kho A - Tầng 1', 1),
    (N'TB-002', N'Loa Bluetooth JBL',          N'Thiết bị điện tử', 10, 10, N'Cái', N'Kho A - Tầng 1', 1),
    (N'SK-001', N'Sách giáo trình IELTS 12',   N'Sách - Tài liệu',  50, 50, N'Cuốn',N'Kho B - Tầng 2', 1),
    (N'VT-001', N'Bảng trắng từ tính',         N'Vật tư văn phòng', 8,  8,  N'Cái', N'Kho A - Tầng 1', 1);
GO

-- ============================================================
-- TABLE: Transactions
-- ============================================================
IF OBJECT_ID('dbo.Transactions', 'U') IS NOT NULL DROP TABLE dbo.Transactions;
GO

CREATE TABLE dbo.Transactions (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    transaction_code NVARCHAR(50)  NOT NULL UNIQUE,
    asset_id         INT           NOT NULL,
    user_id          INT           NOT NULL,       -- Người mượn/trả
    approved_by      INT           NULL,            -- Người duyệt
    transaction_type NVARCHAR(20)  NOT NULL,        -- BORROW | RETURN
    quantity         INT           NOT NULL DEFAULT 1,
    note             NVARCHAR(500) NULL,
    borrow_date      DATETIME2     NULL,
    expected_return  DATETIME2     NULL,
    actual_return    DATETIME2     NULL,
    status           NVARCHAR(20)  NOT NULL DEFAULT N'PENDING',
    -- PENDING | APPROVED | REJECTED | COMPLETED
    created_at       DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at       DATETIME2     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Trans_Assets   FOREIGN KEY (asset_id)     REFERENCES dbo.Assets(id),
    CONSTRAINT FK_Trans_Users    FOREIGN KEY (user_id)      REFERENCES dbo.Users(id),
    CONSTRAINT FK_Trans_Approved FOREIGN KEY (approved_by)  REFERENCES dbo.Users(id),
    CONSTRAINT CHK_Trans_Type    CHECK (transaction_type IN (N'BORROW', N'RETURN')),
    CONSTRAINT CHK_Trans_Status  CHECK (status IN (N'PENDING', N'APPROVED', N'REJECTED', N'COMPLETED')),
    CONSTRAINT CHK_Trans_Qty     CHECK (quantity > 0)
);
GO

-- ============================================================
-- TRIGGER: trg_AfterApprove_Borrow
-- Mô tả: Sau khi giao dịch BORROW được APPROVED,
--        tự động GIẢM available_quantity của tài sản.
-- ============================================================
IF OBJECT_ID('dbo.trg_AfterApprove_Borrow', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_AfterApprove_Borrow;
GO

CREATE TRIGGER dbo.trg_AfterApprove_Borrow
ON dbo.Transactions
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Chỉ xử lý khi status chuyển sang APPROVED và type = BORROW
    IF EXISTS (
        SELECT 1 FROM inserted i
        JOIN deleted d ON i.id = d.id
        WHERE i.status = N'APPROVED'
          AND d.status = N'PENDING'
          AND i.transaction_type = N'BORROW'
    )
    BEGIN
        UPDATE a
        SET a.available_quantity = a.available_quantity - i.quantity,
            a.updated_at         = GETDATE()
        FROM dbo.Assets a
        JOIN inserted i ON a.id = i.asset_id
        JOIN deleted  d ON i.id = d.id
        WHERE i.status = N'APPROVED'
          AND d.status = N'PENDING'
          AND i.transaction_type = N'BORROW';

        -- Kiểm tra sau khi cập nhật có bị âm không
        IF EXISTS (SELECT 1 FROM dbo.Assets WHERE available_quantity < 0)
        BEGIN
            RAISERROR(N'Số lượng tài sản khả dụng không đủ để thực hiện mượn!', 16, 1);
            ROLLBACK TRANSACTION;
        END
    END
END;
GO

-- ============================================================
-- TRIGGER: trg_AfterComplete_Return
-- Mô tả: Sau khi giao dịch RETURN được COMPLETED,
--        tự động CỘNG available_quantity của tài sản.
-- ============================================================
IF OBJECT_ID('dbo.trg_AfterComplete_Return', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_AfterComplete_Return;
GO

CREATE TRIGGER dbo.trg_AfterComplete_Return
ON dbo.Transactions
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Chỉ xử lý khi status chuyển sang COMPLETED và type = RETURN
    IF EXISTS (
        SELECT 1 FROM inserted i
        JOIN deleted d ON i.id = d.id
        WHERE i.status = N'COMPLETED'
          AND d.status = N'APPROVED'
          AND i.transaction_type = N'RETURN'
    )
    BEGIN
        UPDATE a
        SET a.available_quantity = a.available_quantity + i.quantity,
            a.updated_at         = GETDATE()
        FROM dbo.Assets a
        JOIN inserted i ON a.id = i.asset_id
        JOIN deleted  d ON i.id = d.id
        WHERE i.status = N'COMPLETED'
          AND d.status = N'APPROVED'
          AND i.transaction_type = N'RETURN';

        -- Đảm bảo available không vượt quá total
        UPDATE a
        SET a.available_quantity = a.total_quantity
        FROM dbo.Assets a
        WHERE a.available_quantity > a.total_quantity;
    END
END;
GO

PRINT N'=== LangCenterAssetDB Schema created successfully! ===';
GO
- -   A s s e t s   a n d   C a t e g o r i e s  
 