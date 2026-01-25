-- インターネットバンキングシステム サンプルデータ

-- テストユーザーの挿入（パスワード: password123）
INSERT INTO users (username, email, password_hash, first_name, last_name, phone_number, is_enabled, is_locked, mfa_enabled, created_at, updated_at) VALUES
('testuser', 'test@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.gS8v.m', '太郎', '田中', '090-1234-5678', true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', 'admin@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.gS8v.m', '管理者', 'システム', '090-9999-9999', true, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 口座データの挿入
INSERT INTO accounts (user_id, account_number, account_type, balance, currency, status, interest_rate, created_at, updated_at) VALUES
(1, '1234-5678-9012', 'SAVINGS', 500000.00, 'JPY', 'ACTIVE', 0.0010, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '8765-4321-0987', 'FIXED_DEPOSIT', 1000000.00, 'JPY', 'ACTIVE', 0.0050, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '1111-2222-3333', 'CHECKING', 2500000.00, 'JPY', 'ACTIVE', 0.0005, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 取引履歴データの挿入
INSERT INTO transactions (from_account_id, to_account_id, transaction_type, amount, currency, description, status, reference_number, transaction_date, created_at) VALUES
(1, 2, 'TRANSFER', 50000.00, 'JPY', '給料振込', 'COMPLETED', 'TXN001', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(2, 1, 'TRANSFER', 25000.00, 'JPY', '家賃支払い', 'COMPLETED', 'TXN002', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
(1, NULL, 'DEPOSIT', 100000.00, 'JPY', '現金入金', 'COMPLETED', 'TXN003', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP);

-- 定期預金データの挿入
INSERT INTO fixed_deposits (account_id, amount, interest_rate, term_months, start_date, end_date, status, created_at, updated_at) VALUES
(2, 1000000.00, 0.0050, 12, CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ローン情報の挿入
INSERT INTO loans (user_id, loan_type, amount, interest_rate, term_months, monthly_payment, remaining_balance, start_date, end_date, created_at, updated_at) VALUES
(1, 'MORTGAGE', 30000000.00, 0.0250, 360, 120000.00, 28000000.00, CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '354 months', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
