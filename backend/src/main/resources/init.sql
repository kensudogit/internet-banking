-- インターネットバンキングシステム データベース初期化スクリプト
-- Railway PostgreSQL サービスで使用

-- スキーマが存在しない場合のみ作成
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        -- ユーザーテーブル
        CREATE TABLE users (
            id BIGSERIAL PRIMARY KEY,
            username VARCHAR(50) UNIQUE NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            password_hash VARCHAR(255) NOT NULL,
            first_name VARCHAR(50) NOT NULL,
            last_name VARCHAR(50) NOT NULL,
            phone_number VARCHAR(20),
            is_enabled BOOLEAN DEFAULT true,
            is_locked BOOLEAN DEFAULT false,
            mfa_enabled BOOLEAN DEFAULT false,
            mfa_secret VARCHAR(255),
            last_login TIMESTAMP,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- 口座テーブル
        CREATE TABLE accounts (
            id BIGSERIAL PRIMARY KEY,
            user_id BIGINT NOT NULL REFERENCES users(id),
            account_number VARCHAR(20) UNIQUE NOT NULL,
            account_type VARCHAR(20) NOT NULL,
            balance DECIMAL(15,2) DEFAULT 0.00,
            currency VARCHAR(3) DEFAULT 'JPY',
            status VARCHAR(20) DEFAULT 'ACTIVE',
            interest_rate DECIMAL(5,4) DEFAULT 0.0000,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- 取引履歴テーブル
        CREATE TABLE transactions (
            id BIGSERIAL PRIMARY KEY,
            from_account_id BIGINT REFERENCES accounts(id),
            to_account_id BIGINT REFERENCES accounts(id),
            transaction_type VARCHAR(20) NOT NULL,
            amount DECIMAL(15,2) NOT NULL,
            currency VARCHAR(3) DEFAULT 'JPY',
            description TEXT,
            status VARCHAR(20) DEFAULT 'COMPLETED',
            reference_number VARCHAR(50) UNIQUE NOT NULL,
            transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- 定期預金テーブル
        CREATE TABLE fixed_deposits (
            id BIGSERIAL PRIMARY KEY,
            account_id BIGINT NOT NULL REFERENCES accounts(id),
            amount DECIMAL(15,2) NOT NULL,
            interest_rate DECIMAL(5,4) NOT NULL,
            term_months INTEGER NOT NULL,
            start_date DATE NOT NULL,
            end_date DATE NOT NULL,
            status VARCHAR(20) DEFAULT 'ACTIVE',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- ローン情報テーブル
        CREATE TABLE loans (
            id BIGSERIAL PRIMARY KEY,
            user_id BIGINT NOT NULL REFERENCES users(id),
            loan_type VARCHAR(50) NOT NULL,
            amount DECIMAL(15,2) NOT NULL,
            interest_rate DECIMAL(5,4) NOT NULL,
            term_months INTEGER NOT NULL,
            monthly_payment DECIMAL(15,2) NOT NULL,
            remaining_balance DECIMAL(15,2) NOT NULL,
            status VARCHAR(20) DEFAULT 'ACTIVE',
            start_date DATE NOT NULL,
            end_date DATE NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- セキュリティログテーブル
        CREATE TABLE security_logs (
            id BIGSERIAL PRIMARY KEY,
            user_id BIGINT REFERENCES users(id),
            action VARCHAR(50) NOT NULL,
            ip_address INET,
            user_agent TEXT,
            success BOOLEAN DEFAULT true,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        -- インデックス
        CREATE INDEX idx_users_username ON users(username);
        CREATE INDEX idx_users_email ON users(email);
        CREATE INDEX idx_accounts_user_id ON accounts(user_id);
        CREATE INDEX idx_accounts_account_number ON accounts(account_number);
        CREATE INDEX idx_transactions_from_account_id ON transactions(from_account_id);
        CREATE INDEX idx_transactions_to_account_id ON transactions(to_account_id);
        CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);
        CREATE INDEX idx_transactions_reference_number ON transactions(reference_number);
        CREATE INDEX idx_fixed_deposits_account_id ON fixed_deposits(account_id);
        CREATE INDEX idx_loans_user_id ON loans(user_id);
        CREATE INDEX idx_security_logs_user_id ON security_logs(user_id);
        CREATE INDEX idx_security_logs_created_at ON security_logs(created_at);
    END IF;
END $$;

