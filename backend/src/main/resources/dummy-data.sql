-- インターネットバンキングシステム ダミーデータ
-- ユーザーID 1用のダミーデータを生成

-- ユーザーID 1が存在しない場合は作成（パスワード: password123）
INSERT INTO users (id, username, email, password_hash, first_name, last_name, phone_number, is_enabled, is_locked, mfa_enabled, created_at, updated_at)
SELECT 1, 'testuser', 'test@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.gS8v.m', '太郎', '田中', '090-1234-5678', true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

-- 口座データの挿入（ユーザーID 1用）
INSERT INTO accounts (user_id, account_number, account_type, balance, currency, status, interest_rate, created_at, updated_at)
SELECT 1, '1234-5678-9012', 'SAVINGS', 1500000.00, 'JPY', 'ACTIVE', 0.0010, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = '1234-5678-9012')
UNION ALL
SELECT 1, '8765-4321-0987', 'CHECKING', 500000.00, 'JPY', 'ACTIVE', 0.0005, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = '8765-4321-0987')
UNION ALL
SELECT 1, '1111-2222-3333', 'FIXED_DEPOSIT', 2000000.00, 'JPY', 'ACTIVE', 0.0050, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = '1111-2222-3333')
UNION ALL
SELECT 1, '9999-8888-7777', 'SAVINGS', 800000.00, 'JPY', 'ACTIVE', 0.0010, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = '9999-8888-7777');

-- 取引履歴データの挿入（ユーザーID 1の口座用）
-- 口座IDを取得して使用
DO $$
DECLARE
    account1_id BIGINT;
    account2_id BIGINT;
    account3_id BIGINT;
    account4_id BIGINT;
BEGIN
    -- 口座IDを取得
    SELECT id INTO account1_id FROM accounts WHERE account_number = '1234-5678-9012' LIMIT 1;
    SELECT id INTO account2_id FROM accounts WHERE account_number = '8765-4321-0987' LIMIT 1;
    SELECT id INTO account3_id FROM accounts WHERE account_number = '1111-2222-3333' LIMIT 1;
    SELECT id INTO account4_id FROM accounts WHERE account_number = '9999-8888-7777' LIMIT 1;

    -- 取引履歴の挿入
    INSERT INTO transactions (from_account_id, to_account_id, transaction_type, amount, currency, description, status, reference_number, transaction_date, created_at)
    SELECT account1_id, account2_id, 'TRANSFER', 50000.00, 'JPY', '給料振込', 'COMPLETED', 'TXN' || LPAD(CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '給料振込' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '2 days')
    UNION ALL
    SELECT account2_id, account1_id, 'TRANSFER', 30000.00, 'JPY', '家賃支払い', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 1) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '家賃支払い' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '3 days')
    UNION ALL
    SELECT NULL, account1_id, 'DEPOSIT', 100000.00, 'JPY', '現金入金', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 2) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '現金入金' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '4 days')
    UNION ALL
    SELECT account1_id, account3_id, 'TRANSFER', 200000.00, 'JPY', '定期預金への振込', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 3) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '定期預金への振込' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '6 days')
    UNION ALL
    SELECT account1_id, NULL, 'WITHDRAWAL', 50000.00, 'JPY', 'ATM出金', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 4) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = 'ATM出金' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '8 days')
    UNION ALL
    SELECT account2_id, account4_id, 'TRANSFER', 100000.00, 'JPY', '投資口座への振込', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 5) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '投資口座への振込' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '11 days')
    UNION ALL
    SELECT NULL, account1_id, 'DEPOSIT', 150000.00, 'JPY', 'ボーナス振込', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 6) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = 'ボーナス振込' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '16 days')
    UNION ALL
    SELECT account1_id, account2_id, 'TRANSFER', 25000.00, 'JPY', '光熱費支払い', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 7) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '光熱費支払い' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '21 days')
    UNION ALL
    SELECT account2_id, NULL, 'WITHDRAWAL', 30000.00, 'JPY', '現金引き出し', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 8) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '現金引き出し' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '26 days')
    UNION ALL
    SELECT NULL, account1_id, 'DEPOSIT', 50000.00, 'JPY', '給与振込', 'COMPLETED', 'TXN' || LPAD(CAST((EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::bigint + 9) AS TEXT), 10, '0'), CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE description = '給与振込' AND transaction_date >= CURRENT_TIMESTAMP - INTERVAL '31 days');
END $$;

-- 投資情報データの挿入（ユーザーID 1用）
DO $$
DECLARE
    account1_id BIGINT;
    account2_id BIGINT;
    account4_id BIGINT;
BEGIN
    -- 口座IDを取得
    SELECT id INTO account1_id FROM accounts WHERE account_number = '1234-5678-9012' LIMIT 1;
    SELECT id INTO account2_id FROM accounts WHERE account_number = '8765-4321-0987' LIMIT 1;
    SELECT id INTO account4_id FROM accounts WHERE account_number = '9999-8888-7777' LIMIT 1;

    -- 投資情報の挿入
    INSERT INTO investments (user_id, account_id, investment_type, product_name, amount, current_value, purchase_date, status, created_at, updated_at)
    SELECT 1, account1_id, 'MUTUAL_FUND', '日本株式インデックスファンド', 500000.00, 525000.00, CURRENT_DATE - INTERVAL '6 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM investments WHERE product_name = '日本株式インデックスファンド' AND user_id = 1)
    UNION ALL
    SELECT 1, account1_id, 'STOCK', 'トヨタ自動車株式', 300000.00, 315000.00, CURRENT_DATE - INTERVAL '3 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM investments WHERE product_name = 'トヨタ自動車株式' AND user_id = 1)
    UNION ALL
    SELECT 1, account2_id, 'BOND', '10年物国債', 200000.00, 202000.00, CURRENT_DATE - INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM investments WHERE product_name = '10年物国債' AND user_id = 1)
    UNION ALL
    SELECT 1, account4_id, 'ETF', '日経225連動型ETF', 400000.00, 420000.00, CURRENT_DATE - INTERVAL '9 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM investments WHERE product_name = '日経225連動型ETF' AND user_id = 1)
    UNION ALL
    SELECT 1, account1_id, 'MUTUAL_FUND', 'グローバルバランスファンド', 600000.00, 630000.00, CURRENT_DATE - INTERVAL '18 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM investments WHERE product_name = 'グローバルバランスファンド' AND user_id = 1);
END $$;
