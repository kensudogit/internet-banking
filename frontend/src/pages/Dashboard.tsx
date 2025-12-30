import React, { useState, useEffect } from 'react';
import { apiService, Account, Transaction } from '../services/api';
import { 
  BanknotesIcon, 
  ChartBarIcon, 
  ClockIcon, 
  ExclamationTriangleIcon,
  ArrowTrendingUpIcon
} from '@heroicons/react/24/outline';

const Dashboard: React.FC = () => {
  const [userId] = useState(1); // 仮のユーザーID
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [accountsLoading, setAccountsLoading] = useState(true);
  const [transactionsLoading, setTransactionsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setError(null);
        
        // 口座情報と取引履歴を並行して取得
        const [accountsData, transactionsData] = await Promise.all([
          apiService.getAccounts(userId),
          apiService.getTransactions(userId)
        ]);

        setAccounts(accountsData);
        setTransactions(transactionsData);
      } catch (err) {
        console.error('Error fetching data:', err);
        
        // エラーの詳細をログに出力
        if (err instanceof Error) {
          console.error('Error message:', err.message);
          console.error('Error stack:', err.stack);
        }
        
        // エラーメッセージをより具体的に
        let errorMessage = 'データの読み込みに失敗しました。';
        if (err instanceof Error) {
          if (err.message.includes('404')) {
            errorMessage = 'APIエンドポイントが見つかりません。サーバーの設定を確認してください。';
          } else if (err.message.includes('500')) {
            errorMessage = 'サーバーエラーが発生しました。しばらく時間をおいて再度お試しください。';
          } else if (err.message.includes('fetch')) {
            errorMessage = 'ネットワークエラーが発生しました。インターネット接続を確認してください。';
          }
        }
        
        setError(errorMessage);
      } finally {
        setAccountsLoading(false);
        setTransactionsLoading(false);
      }
    };

    fetchData();
  }, [userId]);

  const totalBalance = accounts?.reduce((sum: number, account: Account) => {
    return sum + account.balance;
  }, 0) || 0;

  const recentTransactions = transactions?.slice(0, 5) || [];
  const accountCount = accounts?.length || 0;

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full mx-4">
          <div className="text-center">
            <div className="mx-auto h-16 w-16 bg-red-100 rounded-full flex items-center justify-center mb-4">
              <ExclamationTriangleIcon className="h-8 w-8 text-red-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">エラーが発生しました</h3>
            <p className="text-gray-600 mb-6">{error}</p>
            <button 
              onClick={() => window.location.reload()} 
              className="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
            >
              再読み込み
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (accountsLoading || transactionsLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <div className="text-xl font-semibold text-gray-700">読み込み中...</div>
          <div className="text-gray-500">データを取得しています</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* ヘッダーセクション */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">ダッシュボード</h1>
              <p className="text-gray-600 mt-2">お客様の口座情報と取引履歴をご確認いただけます</p>
            </div>
            <div className="hidden sm:block">
              <div className="bg-primary-50 border border-primary-200 rounded-full px-4 py-2">
                <span className="text-primary-700 text-sm font-medium">最終更新: {new Date().toLocaleTimeString('ja-JP')}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 統計カード */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          {/* 総残高カード */}
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm font-medium">総残高</p>
                <p className="text-3xl font-bold">¥{totalBalance.toLocaleString()}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <BanknotesIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 flex items-center text-blue-100 text-sm">
              <ArrowTrendingUpIcon className="h-4 w-4 mr-1" />
              <span>前月比 +2.5%</span>
            </div>
          </div>

          {/* 口座数カード */}
          <div className="bg-gradient-to-r from-green-500 to-green-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-sm font-medium">保有口座数</p>
                <p className="text-3xl font-bold">{accountCount}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ChartBarIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 flex items-center text-green-100 text-sm">
              <span>アクティブな口座</span>
            </div>
          </div>

          {/* 最近の取引カード */}
          <div className="bg-gradient-to-r from-purple-500 to-purple-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-purple-100 text-sm font-medium">最近の取引</p>
                <p className="text-3xl font-bold">{recentTransactions.length}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ClockIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 flex items-center text-purple-100 text-sm">
              <span>過去30日間</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* 口座一覧 */}
          <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
            <div className="bg-gradient-to-r from-gray-50 to-gray-100 px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                <BanknotesIcon className="h-5 w-5 mr-2 text-primary-600" />
                口座一覧
              </h3>
            </div>
            {accounts.length === 0 ? (
              <div className="p-8 text-center">
                <div className="mx-auto h-16 w-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                  <BanknotesIcon className="h-8 w-8 text-gray-400" />
                </div>
                <p className="text-gray-500 text-lg">口座情報が見つかりません</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-100">
                {accounts.map((account: Account) => (
                  <div key={account.id} className="p-6 hover:bg-gray-50 transition-colors duration-200">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className="h-12 w-12 rounded-xl bg-gradient-to-r from-primary-500 to-primary-600 flex items-center justify-center">
                          <span className="text-white font-bold text-lg">
                            {account.accountType.charAt(0)}
                          </span>
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {account.accountNumber}
                          </div>
                          <div className="text-sm text-gray-500 capitalize">
                            {account.accountType.toLowerCase().replace('_', ' ')}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="text-lg font-bold text-gray-900">
                          ¥{account.balance.toLocaleString()}
                        </div>
                        <div className="text-sm text-gray-500">
                          {account.currency}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* 最近の取引 */}
          <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
            <div className="bg-gradient-to-r from-gray-50 to-gray-100 px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                <ClockIcon className="h-5 w-5 mr-2 text-primary-600" />
                最近の取引
              </h3>
            </div>
            {recentTransactions.length === 0 ? (
              <div className="p-8 text-center">
                <div className="mx-auto h-16 w-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                  <ClockIcon className="h-8 w-8 text-gray-400" />
                </div>
                <p className="text-gray-500 text-lg">取引履歴が見つかりません</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-100">
                {recentTransactions.map((transaction: Transaction) => (
                  <div key={transaction.id} className="p-6 hover:bg-gray-50 transition-colors duration-200">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className={`h-10 w-10 rounded-full flex items-center justify-center ${
                          transaction.transactionType === 'TRANSFER' ? 'bg-blue-100' :
                          transaction.transactionType === 'DEPOSIT' ? 'bg-green-100' :
                          'bg-red-100'
                        }`}>
                          <span className={`text-sm font-medium ${
                            transaction.transactionType === 'TRANSFER' ? 'text-blue-600' :
                            transaction.transactionType === 'DEPOSIT' ? 'text-green-600' :
                            'text-red-600'
                          }`}>
                            {transaction.transactionType.charAt(0)}
                          </span>
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {transaction.description}
                          </div>
                          <div className="text-sm text-gray-500">
                            {new Date(transaction.transactionDate).toLocaleDateString('ja-JP')}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className={`text-sm font-bold ${
                          transaction.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {transaction.transactionType === 'DEPOSIT' ? '+' : '-'}¥{transaction.amount.toLocaleString()}
                        </div>
                        <div className="text-xs text-gray-500 capitalize">
                          {transaction.status.toLowerCase()}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* クイックアクション */}
        <div className="mt-8 bg-white rounded-2xl shadow-lg p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">クイックアクション</h3>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <button className="bg-primary-50 hover:bg-primary-100 text-primary-700 font-medium py-3 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center">
              <BanknotesIcon className="h-5 w-5 mr-2" />
              振込
            </button>
            <button className="bg-green-50 hover:bg-green-100 text-green-700 font-medium py-3 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center">
              <ChartBarIcon className="h-5 w-5 mr-2" />
              投資
            </button>
            <button className="bg-purple-50 hover:bg-purple-100 text-purple-700 font-medium py-3 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center">
              <ClockIcon className="h-5 w-5 mr-2" />
              取引履歴
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
