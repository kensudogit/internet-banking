import { mockApiService, shouldUseMockApi } from './mockApi';

const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

// デバッグ用のログ出力
console.log('API_BASE_URL:', API_BASE_URL);
console.log('Environment:', process.env.REACT_APP_ENV || 'development');
console.log('Should use mock API:', shouldUseMockApi());

export interface Account {
  id: number;
  userId: number;
  accountNumber: string;
  accountType: string;
  balance: number;
  currency: string;
  status: string;
  interestRate: number;
}

export interface Transaction {
  id: number;
  fromAccountId: number;
  toAccountId: number | null;
  transactionType: string;
  amount: number;
  currency: string;
  description: string;
  status: string;
  referenceNumber: string;
  transactionDate: string;
}

export const apiService = {
  // 口座情報を取得
  async getAccounts(userId: number): Promise<Account[]> {
    // モックAPIを使用する場合
    if (shouldUseMockApi()) {
      console.log('Using mock API for accounts');
      return mockApiService.getAccounts(userId);
    }

    try {
      const url = `${API_BASE_URL}/accounts/user/${userId}`;
      console.log('Fetching accounts from:', url);
      
      const response = await fetch(url);
      console.log('Accounts response status:', response.status);
      console.log('Accounts response headers:', response.headers);
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Accounts API error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
      
      const data = await response.json();
      console.log('Accounts data received:', data);
      return data;
    } catch (error) {
      console.error('Error fetching accounts:', error);
      
      // APIが利用できない場合、モックAPIにフォールバック
      console.log('Falling back to mock API for accounts');
      return mockApiService.getAccounts(userId);
    }
  },

  // 取引履歴を取得
  async getTransactions(userId: number): Promise<Transaction[]> {
    // モックAPIを使用する場合
    if (shouldUseMockApi()) {
      console.log('Using mock API for transactions');
      return mockApiService.getTransactions(userId);
    }

    try {
      const url = `${API_BASE_URL}/transactions/user/${userId}`;
      console.log('Fetching transactions from:', url);
      
      const response = await fetch(url);
      console.log('Transactions response status:', response.status);
      console.log('Transactions response headers:', response.headers);
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Transactions API error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
      
      const data = await response.json();
      console.log('Transactions data received:', data);
      return data;
    } catch (error) {
      console.error('Error fetching transactions:', error);
      
      // APIが利用できない場合、モックAPIにフォールバック
      console.log('Falling back to mock API for transactions');
      return mockApiService.getTransactions(userId);
    }
  }
};
