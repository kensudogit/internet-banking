import React, { useState, useEffect } from 'react';
import { apiService, Transaction } from '../services/api';
import { 
  ClockIcon, 
  ArrowDownTrayIcon,
  ExclamationTriangleIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  BanknotesIcon,
  CreditCardIcon
} from '@heroicons/react/24/outline';

const Transactions: React.FC = () => {
  const [userId] = useState(1);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filterType, setFilterType] = useState<string>('ALL');
  const [sortBy, setSortBy] = useState<string>('date');

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        setError(null);
        const transactionsData = await apiService.getTransactions(userId);
        setTransactions(transactionsData);
      } catch (err) {
        console.error('Error fetching transactions:', err);
        setError('取引履歴の取得に失敗しました。');
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [userId]);

  const getTransactionIcon = (transactionType: string) => {
    switch (transactionType) {
      case 'TRANSFER':
        return ArrowTrendingUpIcon;
      case 'DEPOSIT':
        return ArrowTrendingDownIcon;
      case 'WITHDRAWAL':
        return BanknotesIcon;
      case 'PAYMENT':
        return CreditCardIcon;
      default:
        return ClockIcon;
    }
  };

  const getTransactionColor = (transactionType: string) => {
    switch (transactionType) {
      case 'TRANSFER':
        return 'bg-blue-100 text-blue-600';
      case 'DEPOSIT':
        return 'bg-green-100 text-green-600';
      case 'WITHDRAWAL':
        return 'bg-red-100 text-red-600';
      case 'PAYMENT':
        return 'bg-purple-100 text-purple-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  };

  const getTransactionTypeLabel = (transactionType: string) => {
    switch (transactionType) {
      case 'TRANSFER':
        return '振込';
      case 'DEPOSIT':
        return '入金';
      case 'WITHDRAWAL':
        return '出金';
      case 'PAYMENT':
        return '支払い';
      default:
        return transactionType;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return '完了';
      case 'PENDING':
        return '処理中';
      case 'FAILED':
        return '失敗';
      default:
        return status;
    }
  };

  const filteredTransactions = transactions.filter(transaction => {
    if (filterType === 'ALL') return true;
    return transaction.transactionType === filterType;
  });

  const sortedTransactions = [...filteredTransactions].sort((a, b) => {
    if (sortBy === 'date') {
      return new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime();
    }
    if (sortBy === 'amount') {
      return b.amount - a.amount;
    }
    return 0;
  });

  const totalIncome = transactions
    .filter(t => t.transactionType === 'DEPOSIT')
    .reduce((sum, t) => sum + t.amount, 0);

  const totalExpense = transactions
    .filter(t => t.transactionType !== 'DEPOSIT')
    .reduce((sum, t) => sum + t.amount, 0);

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

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <div className="text-xl font-semibold text-gray-700">読み込み中...</div>
          <div className="text-gray-500">取引履歴を取得しています</div>
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
              <h1 className="text-3xl font-bold text-gray-900">取引履歴</h1>
              <p className="text-gray-600 mt-2">お客様の取引履歴と詳細情報をご確認いただけます</p>
            </div>
            <div className="flex items-center space-x-4">
              <button className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center">
                <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
                CSV出力
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 統計カード */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          {/* 総取引数 */}
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm font-medium">総取引数</p>
                <p className="text-3xl font-bold">{transactions.length}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ClockIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 text-blue-100 text-sm">
              過去30日間の取引
            </div>
          </div>

          {/* 総入金 */}
          <div className="bg-gradient-to-r from-green-500 to-green-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-sm font-medium">総入金</p>
                <p className="text-3xl font-bold">¥{totalIncome.toLocaleString()}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ArrowTrendingDownIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 text-green-100 text-sm">
              入金取引の合計
            </div>
          </div>

          {/* 総出金 */}
          <div className="bg-gradient-to-r from-red-500 to-red-600 rounded-2xl shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-red-100 text-sm font-medium">総出金</p>
                <p className="text-3xl font-bold">¥{totalExpense.toLocaleString()}</p>
              </div>
              <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ArrowTrendingUpIcon className="h-6 w-6 text-white" />
              </div>
            </div>
            <div className="mt-4 text-red-100 text-sm">
              出金取引の合計
            </div>
          </div>
        </div>

        {/* フィルターとソート */}
        <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
            <div className="flex items-center space-x-4">
              <label className="text-sm font-medium text-gray-700">取引種別:</label>
              <select
                value={filterType}
                onChange={(e) => setFilterType(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="ALL">すべて</option>
                <option value="TRANSFER">振込</option>
                <option value="DEPOSIT">入金</option>
                <option value="WITHDRAWAL">出金</option>
                <option value="PAYMENT">支払い</option>
              </select>
            </div>
            
            <div className="flex items-center space-x-4">
              <label className="text-sm font-medium text-gray-700">並び順:</label>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="date">日付順</option>
                <option value="amount">金額順</option>
              </select>
            </div>
          </div>
        </div>

        {/* 取引一覧 */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div className="bg-gradient-to-r from-gray-50 to-gray-100 px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900 flex items-center">
              <ClockIcon className="h-5 w-5 mr-2 text-primary-600" />
              取引一覧 ({sortedTransactions.length}件)
            </h3>
          </div>
          
          {sortedTransactions.length === 0 ? (
            <div className="p-12 text-center">
              <div className="mx-auto h-24 w-24 bg-gray-100 rounded-full flex items-center justify-center mb-6">
                <ClockIcon className="h-12 w-12 text-gray-400" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">取引履歴がありません</h3>
              <p className="text-gray-600">指定された条件に一致する取引が見つかりません</p>
            </div>
          ) : (
            <div className="divide-y divide-gray-100">
              {sortedTransactions.map((transaction) => {
                const IconComponent = getTransactionIcon(transaction.transactionType);
                const colorClass = getTransactionColor(transaction.transactionType);
                
                return (
                  <div key={transaction.id} className="p-6 hover:bg-gray-50 transition-colors duration-200">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className={`h-12 w-12 rounded-xl flex items-center justify-center ${colorClass}`}>
                          <IconComponent className="h-6 w-6" />
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {transaction.description}
                          </div>
                          <div className="text-sm text-gray-500">
                            {getTransactionTypeLabel(transaction.transactionType)} • {transaction.referenceNumber}
                          </div>
                          <div className="text-sm text-gray-500">
                            {new Date(transaction.transactionDate).toLocaleDateString('ja-JP', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </div>
                        </div>
                      </div>
                      
                      <div className="text-right">
                        <div className={`text-lg font-bold ${
                          transaction.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {transaction.transactionType === 'DEPOSIT' ? '+' : '-'}¥{transaction.amount.toLocaleString()}
                        </div>
                        <div className="text-sm text-gray-500">
                          {transaction.currency}
                        </div>
                        <div className={`mt-1 inline-flex px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(transaction.status)}`}>
                          {getStatusLabel(transaction.status)}
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* 取引統計 */}
        {transactions.length > 0 && (
          <div className="mt-8 bg-white rounded-2xl shadow-lg p-8">
            <h3 className="text-xl font-semibold text-gray-900 mb-6">取引統計</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div>
                <h4 className="text-lg font-medium text-gray-900 mb-4">取引種別別件数</h4>
                <div className="space-y-3">
                  {['TRANSFER', 'DEPOSIT', 'WITHDRAWAL', 'PAYMENT'].map((type) => {
                    const count = transactions.filter(t => t.transactionType === type).length;
                    const percentage = transactions.length > 0 ? (count / transactions.length * 100).toFixed(1) : 0;
                    
                    return (
                      <div key={type} className="flex items-center justify-between">
                        <span className="text-sm text-gray-600">{getTransactionTypeLabel(type)}</span>
                        <div className="flex items-center space-x-3">
                          <div className="w-24 bg-gray-200 rounded-full h-2">
                            <div 
                              className="bg-primary-600 h-2 rounded-full" 
                              style={{ width: `${percentage}%` }}
                            ></div>
                          </div>
                          <span className="text-sm font-medium text-gray-900">{count}件</span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
              
              <div>
                <h4 className="text-lg font-medium text-gray-900 mb-4">月別取引推移</h4>
                <div className="text-center py-8">
                  <div className="text-gray-500 text-sm">グラフ表示予定</div>
                  <p className="text-xs text-gray-400 mt-2">今後、月別の取引推移グラフを追加予定です</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Transactions;
