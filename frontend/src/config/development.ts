// 開発環境用設定
export const developmentConfig = {
  // API設定
  api: {
    baseUrl: 'http://localhost:3000/api',
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 1000,
  },
  
  // モック設定
  mock: {
    enabled: true,
    delay: {
      accounts: 500,
      transactions: 800,
    },
  },
  
  // ログ設定
  logging: {
    level: 'debug',
    enableConsole: true,
    enableNetwork: true,
  },
  
  // 機能フラグ
  features: {
    enableMFA: true,
    enableLoanManagement: true,
    enableFixedDeposit: true,
    enableAdvancedAnalytics: false,
  },
  
  // 開発ツール
  devTools: {
    enableMockApi: true,
    enableErrorBoundary: true,
    enablePerformanceMonitoring: true,
  },
};

// 環境変数から設定を取得
export const getConfig = () => {
  return {
    ...developmentConfig,
    api: {
      ...developmentConfig.api,
      baseUrl: process.env.REACT_APP_API_URL || developmentConfig.api.baseUrl,
    },
    mock: {
      ...developmentConfig.mock,
      enabled: process.env.REACT_APP_USE_MOCK_API === 'true' || developmentConfig.mock.enabled,
    },
  };
};

