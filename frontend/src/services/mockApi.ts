// 開発用モックAPIサービス
// バックエンドに接続できない場合に自動的に使用されます

import { Account, Transaction, AuthResponse, RegisterRequest, LoginRequest } from './api';

// モックデータ
const mockAccounts: Account[] = [
  {
    id: 1,
    userId: 1,
    accountNumber: '1234-5678-9012',
    accountType: 'SAVINGS',
    balance: 500000.00,
    currency: 'JPY',
    status: 'ACTIVE',
    interestRate: 0.0010
  },
  {
    id: 2,
    userId: 1,
    accountNumber: '8765-4321-0987',
    accountType: 'FIXED_DEPOSIT',
    balance: 1000000.00,
    currency: 'JPY',
    status: 'ACTIVE',
    interestRate: 0.0050
  }
];

const mockTransactions: Transaction[] = [
  {
    id: 1,
    fromAccountId: 1,
    toAccountId: 2,
    transactionType: 'TRANSFER',
    amount: 50000.00,
    currency: 'JPY',
    description: '給料振込',
    status: 'COMPLETED',
    referenceNumber: 'TXN001',
    transactionDate: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    fromAccountId: 2,
    toAccountId: 1,
    transactionType: 'TRANSFER',
    amount: 25000.00,
    currency: 'JPY',
    description: '家賃支払い',
    status: 'COMPLETED',
    referenceNumber: 'TXN002',
    transactionDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString()
  }
];

// モックユーザーデータ
const mockUsers = [
  {
    username: 'testuser',
    password: 'password123',
    email: 'test@example.com',
    firstName: 'Test',
    lastName: 'User',
    userId: 1
  },
  {
    username: 'demo',
    password: 'demo123',
    email: 'demo@example.com',
    firstName: 'Demo',
    lastName: 'User',
    userId: 2
  }
];

// モックAPIサービス
export const mockApiService = {
  // ユーザー登録
  async register(data: RegisterRequest): Promise<AuthResponse> {
    console.log('Mock API: Registering user', data.username);
    
    // 実際のAPIと同様の遅延をシミュレート
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // ユーザー名の重複チェック
    const existingUser = mockUsers.find(u => u.username === data.username);
    if (existingUser) {
      console.log('Mock API: Username already exists');
      return { error: 'このユーザー名は既に使用されています' };
    }
    
    // メールアドレスの重複チェック
    const existingEmail = mockUsers.find(u => u.email === data.email);
    if (existingEmail) {
      console.log('Mock API: Email already exists');
      return { error: 'このメールアドレスは既に使用されています' };
    }
    
    // 新しいユーザーを追加
    const newUserId = mockUsers.length + 1;
    mockUsers.push({
      username: data.username,
      password: data.password,
      email: data.email,
      firstName: data.firstName,
      lastName: data.lastName,
      userId: newUserId
    });
    
    console.log('Mock API: User registered successfully');
    return { message: 'ユーザー登録が完了しました' };
  },

  // ログイン
  async login(data: LoginRequest): Promise<AuthResponse> {
    console.log('Mock API: Logging in user', data.username);
    
    // 実際のAPIと同様の遅延をシミュレート
    await new Promise(resolve => setTimeout(resolve, 600));
    
    // ユーザー認証
    const user = mockUsers.find(
      u => u.username === data.username && u.password === data.password
    );
    
    if (!user) {
      console.log('Mock API: Authentication failed');
      return { error: '認証に失敗しました' };
    }
    
    console.log('Mock API: Login successful for user', user.username);
    return { message: 'ログイン成功', token: `mock-token-${user.userId}` };
  },

  // 口座情報を取得
  async getAccounts(userId: number): Promise<Account[]> {
    console.log('Mock API: Getting accounts for user', userId);
    
    // 実際のAPIと同様の遅延をシミュレート
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const userAccounts = mockAccounts.filter(account => account.userId === userId);
    console.log('Mock API: Returning accounts', userAccounts);
    
    return userAccounts;
  },

  // 取引履歴を取得
  async getTransactions(userId: number): Promise<Transaction[]> {
    console.log('Mock API: Getting transactions for user', userId);
    
    // 実際のAPIと同様の遅延をシミュレート
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // ユーザーの口座IDを取得
    const userAccountIds = mockAccounts
      .filter(account => account.userId === userId)
      .map(account => account.id);
    
    const userTransactions = mockTransactions.filter(transaction => 
      userAccountIds.includes(transaction.fromAccountId) || 
      (transaction.toAccountId !== null && userAccountIds.includes(transaction.toAccountId))
    );
    
    // 取引履歴を日付順でソート（新しい順）
    userTransactions.sort((a, b) => new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime());
    
    console.log('Mock API: Returning transactions', userTransactions);
    
    return userTransactions;
  }
};

// モックAPIを使用するかどうかの判定
export const shouldUseMockApi = (): boolean => {
  // 開発環境またはAPIが利用できない場合
  return process.env.NODE_ENV === 'development' || 
         process.env.REACT_APP_USE_MOCK_API === 'true';
};

