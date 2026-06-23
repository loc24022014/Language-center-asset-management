-- ===========================================================
-- Script: Mock Data for LangCenterAssetDB
-- ===========================================================

-- 1. Insert Roles (if not exist)
IF NOT EXISTS (SELECT 1 FROM Roles WHERE role_name = 'ADMIN')
BEGIN
    INSERT INTO Roles (role_name) VALUES ('ADMIN'), ('MANAGER'), ('TEACHER'), ('STAFF');
END

-- 2. Insert Assets
IF NOT EXISTS (SELECT 1 FROM Assets)
BEGIN
    INSERT INTO Assets (asset_code, name, category, description, total_quantity, available_quantity)
    VALUES
    ('PROJ-001', N'Máy chiếu Panasonic', N'Thiết bị điện tử', N'Máy chiếu độ phân giải 4K, phòng 101', 5, 5),
    ('LAP-001', N'Laptop Dell Latitude', N'Thiết bị điện tử', N'Laptop dùng cho giáo viên', 10, 10),
    ('MIC-001', N'Micro không dây', N'Âm thanh', N'Micro không dây cài áo', 20, 20),
    ('BRD-001', N'Bảng từ trắng', N'Vật tư phòng học', N'Bảng từ trắng 1m2 x 2m', 15, 15),
    ('SPK-001', N'Loa Bluetooth Sony', N'Âm thanh', N'Loa dùng cho phòng nghe', 8, 8);
END
