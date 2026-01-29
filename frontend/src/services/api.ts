import { mockApiService, shouldUseMockApi } from './mockApi';

// 環境変数が設定されている場合はそれを使用、そうでない場合は実行時に判断
// Railway本番環境では、REACT_APP_API_URLが設定されている必要がある
// 開発環境では相対パス(/api)を使用してプロキシ経由でバックエンドに接続
const getApiBaseUrl = () => {
  // 環境変数が明示的に設定されている場合はそれを使用（Railway本番環境）
  if (process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }
  // 開発環境では相対パスを使用（package.jsonのproxy設定によりlocalhost:8080にプロキシ）
  // Railway本番環境では、REACT_APP_API_URLが設定されていない場合、エラーを表示
  const hostname = window.location.hostname;
  if (hostname === 'localhost' || hostname === '127.0.0.1') {
    return '/api'; // 開発環境では相対パスを使用
  }
  // Railway本番環境でREACT_APP_API_URLが設定されていない場合
  // エラーメッセージを表示（実際のエラーは各API呼び出しで処理される）
  console.error('⚠️ REACT_APP_API_URLが設定されていません。Railway Dashboardで環境変数を設定してください。');
  return '/api'; // フォールバック（エラーが発生する）
};

const API_BASE_URL = getApiBaseUrl();

// デバッグ用のログ出力
console.log('API_BASE_URL:', API_BASE_URL);
console.log('Environment:', process.env.REACT_APP_ENV || 'development');
console.log('Should use mock API:', shouldUseMockApi());
console.log('Current hostname:', window.location.hostname);
console.log('Current origin:', window.location.origin);

// Railway本番環境でREACT_APP_API_URLが設定されているか確認
if (window.location.hostname.includes('railway.app') || window.location.hostname.includes('up.railway.app')) {
  if (!process.env.REACT_APP_API_URL) {
    console.error('⚠️ Railway本番環境でREACT_APP_API_URLが設定されていません！');
  } else {
    console.log('✅ REACT_APP_API_URLが設定されています:', process.env.REACT_APP_API_URL);
  }
}

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

export interface Investment {
  id: number;
  userId: number;
  accountId: number;
  investmentType: string;
  productName: string;
  amount: number;
  currentValue: number;
  purchaseDate: string;
  status: string;
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
    // モックAPIを使用する場合
    if (shouldUseMockApi()) {
      console.log('Using mock API for register');
      return mockApiService.register(data);
    }

    try {
      const url = `${API_BASE_URL}/auth/register`;
      console.log('Registering user:', url);
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'omit',
      });

      console.log('Register response status:', response.status);
      
      // Content-Typeをチェックして、JSONでない場合はエラー
      const contentType = response.headers.get('content-type');
      const isJson = contentType && contentType.includes('application/json');
      
      if (!isJson) {
        const errorText = await response.text().catch(() => 'Unknown error');
        console.error('Register API returned non-JSON response:', errorText.substring(0, 200));
        console.log('⚠️ バックエンドがJSON以外のレスポンスを返しました。MOCK APIにフォールバックします。');
        return mockApiService.register(data);
      }

      if (!response.ok) {
        // HTTP 5xxエラーの場合、MOCK APIにフォールバック
        if (response.status >= 500) {
          console.error('Register API returned 5xx error, falling back to mock API');
          return mockApiService.register(data);
        }
        
        const errorData = await response.json().catch(() => ({ error: '登録に失敗しました' }));
        console.error('Register API error response:', errorData);
        throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Register success:', result);
      return result;
    } catch (error: any) {
      console.error('Error registering user:', error);
      console.error('Error details:', {
        message: error.message,
        name: error.name,
        stack: error.stack
      });
      
      // ネットワークエラー、CORSエラー、またはDB接続エラーの場合、MOCK APIにフォールバック
      const errorMessage = error.message || error.toString() || '';
      const errorName = error.name || '';
      
      if (
        errorMessage === 'Failed to fetch' || 
        errorName === 'TypeError' || 
        errorMessage.includes('fetch') ||
        errorMessage.includes('CORS') ||
        errorMessage.includes('ERR_FAILED') ||
        errorMessage.includes('ERR_CONNECTION_REFUSED') ||
        errorMessage.includes('NetworkError') ||
        errorMessage.includes('Network request failed')
      ) {
        console.log('⚠️ バックエンドAPIに接続できません。MOCK APIにフォールバックします。');
        console.log('エラー詳細:', errorMessage);
        return mockApiService.register(data);
      }
      
      throw error;
    }
  },

  // ログイン
  async login(data: LoginRequest): Promise<AuthResponse> {
    // モックAPIを使用する場合
    if (shouldUseMockApi()) {
      console.log('Using mock API for login');
      return mockApiService.login(data);
    }

    try {
      const url = `${API_BASE_URL}/auth/login`;
      console.log('Logging in:', url);
      console.log('Request details:', {
        method: 'POST',
        url: url,
        origin: window.location.origin,
        headers: { 'Content-Type': 'application/json' }
      });
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        // CORSエラーを確認するため、credentialsを明示的に設定
        credentials: 'omit',
      });

      console.log('Login response status:', response.status);
      console.log('Login response headers:', {
        'content-type': response.headers.get('content-type'),
        'access-control-allow-origin': response.headers.get('access-control-allow-origin'),
        'access-control-allow-methods': response.headers.get('access-control-allow-methods'),
      });
      
      // Content-Typeをチェックして、JSONでない場合はエラー
      const contentType = response.headers.get('content-type');
      const isJson = contentType && contentType.includes('application/json');
      
      if (!isJson) {
        const errorText = await response.text().catch(() => 'Unknown error');
        console.error('Login API returned non-JSON response:', errorText.substring(0, 200));
        throw new Error(`バックエンドがJSON以外のレスポンスを返しました。バックエンドサービスの状態を確認してください。ステータス: ${response.status}`);
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'ログインに失敗しました' }));
        console.error('Login API error response:', errorData);
        throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Login success:', result);
      return result;
    } catch (error: any) {
      console.error('Error logging in:', error);
      console.error('Error details:', {
        message: error.message,
        name: error.name,
        stack: error.stack,
        cause: error.cause
      });
      
      // ネットワークエラー、CORSエラー、またはDB接続エラーの場合、MOCK APIにフォールバック
      const errorMessage = error.message || error.toString() || '';
      const errorName = error.name || '';
      
      if (
        errorMessage === 'Failed to fetch' || 
        errorName === 'TypeError' || 
        errorMessage.includes('fetch') ||
        errorMessage.includes('CORS') ||
        errorMessage.includes('ERR_FAILED') ||
        errorMessage.includes('ERR_CONNECTION_REFUSED') ||
        errorMessage.includes('NetworkError') ||
        errorMessage.includes('Network request failed') ||
        errorMessage.includes('blocked by CORS policy')
      ) {
        console.log('⚠️ バックエンドAPIに接続できません。MOCK APIにフォールバックします。');
        console.log('エラー詳細:', errorMessage);
        console.log('エラー名:', errorName);
        return mockApiService.login(data);
      }
      
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
  },

  // 投資情報を取得
  async getInvestments(userId: number): Promise<Investment[]> {
    try {
      const url = `${API_BASE_URL}/investments/user/${userId}`;
      console.log('Fetching investments from:', url);
      
      const response = await fetch(url);
      console.log('Investments response status:', response.status);
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Investments API error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
      
      const data = await response.json();
      console.log('Investments data received:', data);
      return data;
    } catch (error) {
      console.error('Error fetching investments:', error);
      return [];
    }
  },

  // 振込を実行
  async createTransfer(fromAccountId: number, toAccountId: number, amount: number, description?: string): Promise<Transaction> {
    try {
      const url = `${API_BASE_URL}/transactions/transfer`;
      console.log('Creating transfer:', url);
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          fromAccountId,
          toAccountId,
          amount,
          currency: 'JPY',
          description: description || '振込',
        }),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Transfer API error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
      
      const data = await response.json();
      console.log('Transfer created:', data);
      return data;
    } catch (error) {
      console.error('Error creating transfer:', error);
      throw error;
    }
  },

  // 口座を作成
  async createAccount(userId: number, accountType: string, currency: string, interestRate: number): Promise<Account> {
    try {
      const url = `${API_BASE_URL}/accounts`;
      console.log('Creating account:', url);
      
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId,
          accountType,
          currency,
          interestRate,
        }),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Create account API error response:', errorText);
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }
      
      const data = await response.json();
      console.log('Account created:', data);
      return data;
    } catch (error) {
      console.error('Error creating account:', error);
      throw error;
    }
  }
};
