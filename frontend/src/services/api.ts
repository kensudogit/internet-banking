import { mockApiService, shouldUseMockApi } from './mockApi';

// 環境変数が設定されている場合はそれを使用、そうでない場合は実行時に判断
// 開発環境（localhost:3000）ではバックエンドの絶対URLを使用
const getApiBaseUrl = () => {
  if (process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }
  // 実行時にホスト名を確認
  const hostname = window.location.hostname;
  if (hostname === 'localhost' || hostname === '127.0.0.1') {
    return 'http://localhost:8080/api';
  }
  return '/api';
};

const API_BASE_URL = getApiBaseUrl();

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

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  message?: string;
  error?: string;
  token?: string;
}

export const apiService = {
  // ユーザー登録
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const url = `${API_BASE_URL}/auth/register`;
      console.log('Registering user:', url);
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      console.log('Register response status:', response.status);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: '登録に失敗しました' }));
        console.error('Register API error response:', errorData);
        throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Register success:', result);
      return result;
    } catch (error) {
      console.error('Error registering user:', error);
      throw error;
    }
  },

  // ログイン
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const url = `${API_BASE_URL}/auth/login`;
      console.log('Logging in:', url);
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      console.log('Login response status:', response.status);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'ログインに失敗しました' }));
        console.error('Login API error response:', errorData);
        throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Login success:', result);
      return result;
    } catch (error) {
      console.error('Error logging in:', error);
      throw error;
    }
  },

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
