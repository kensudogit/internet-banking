// 開発用モックAPIサービス
// 本番環境では使用しない

import { Account, Transaction } from './api';

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

// モックAPIサービス
export const mockApiService = {
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

