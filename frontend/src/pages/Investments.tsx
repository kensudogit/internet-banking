import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService, Investment } from '../services/api';
import { 
  ChartBarIcon, 
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  ExclamationTriangleIcon
} from '@heroicons/react/24/outline';

const Investments: React.FC = () => {
  const [userId] = useState(1); // 仮のユーザーID
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchInvestments = async () => {
      try {
        setError(null);
        const investmentsData = await apiService.getInvestments(userId);
        setInvestments(investmentsData);
      } catch (err) {
        console.error('Error fetching investments:', err);
        setError('投資情報の取得に失敗しました。');
      } finally {
        setLoading(false);
      }
    };

    fetchInvestments();
  }, [userId]);

  const totalInvestment = investments.reduce((sum, inv) => sum + inv.amount, 0);
  const totalCurrentValue = investments.reduce((sum, inv) => sum + inv.currentValue, 0);
  const totalProfit = totalCurrentValue - totalInvestment;
  const totalProfitRate = totalInvestment > 0 ? (totalProfit / totalInvestment) * 100 : 0;

  const getInvestmentTypeLabel = (type: string) => {
    switch (type) {
      case 'MUTUAL_FUND':
        return '投資信託';
      case 'STOCK':
        return '株式';
      case 'BOND':
        return '債券';
      case 'ETF':
        return 'ETF';
      default:
        return type;
    }
  };

  const getInvestmentTypeColor = (type: string) => {
    switch (type) {
      case 'MUTUAL_FUND':
        return 'from-blue-500 to-blue-600';
      case 'STOCK':
        return 'from-green-500 to-green-600';
      case 'BOND':
        return 'from-purple-500 to-purple-600';
      case 'ETF':
        return 'from-orange-500 to-orange-600';
      default:
        return 'from-gray-500 to-gray-600';
    }
  };

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
          <div className="text-gray-500">投資情報を取得しています</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* ヘッダーセクション */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">投資・資産運用</h1>
              <p className="text-gray-600 mt-1 text-sm">お客様の投資ポートフォリオと運用実績をご確認いただけます</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        {/* 統計カード */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          {/* 投資総額 */}
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl shadow-lg p-4 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-xs font-medium mb-1">投資総額</p>
                <p className="text-2xl font-bold">¥{totalInvestment.toLocaleString()}</p>
              </div>
              <div className="h-10 w-10 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ChartBarIcon className="h-5 w-5 text-white" />
              </div>
            </div>
          </div>

          {/* 現在の価値 */}
          <div className="bg-gradient-to-r from-green-500 to-green-600 rounded-2xl shadow-lg p-4 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-xs font-medium mb-1">現在の価値</p>
                <p className="text-2xl font-bold">¥{totalCurrentValue.toLocaleString()}</p>
              </div>
              <div className="h-10 w-10 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <ArrowTrendingUpIcon className="h-5 w-5 text-white" />
              </div>
            </div>
          </div>

          {/* 損益 */}
          <div className={`bg-gradient-to-r ${totalProfit >= 0 ? 'from-green-500 to-green-600' : 'from-red-500 to-red-600'} rounded-2xl shadow-lg p-4 text-white`}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-white text-xs font-medium mb-1 opacity-90">損益</p>
                <p className="text-2xl font-bold">
                  {totalProfit >= 0 ? '+' : ''}¥{totalProfit.toLocaleString()}
                </p>
                <p className="text-xs mt-1 opacity-90">
                  ({totalProfitRate >= 0 ? '+' : ''}{totalProfitRate.toFixed(2)}%)
                </p>
              </div>
              <div className="h-10 w-10 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                {totalProfit >= 0 ? (
                  <ArrowTrendingUpIcon className="h-5 w-5 text-white" />
                ) : (
                  <ArrowTrendingDownIcon className="h-5 w-5 text-white" />
                )}
              </div>
            </div>
          </div>
        </div>

        {/* 投資一覧 */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="bg-gradient-to-r from-gray-50 to-gray-100 px-4 py-3 border-b border-gray-200">
            <h3 className="text-base font-semibold text-gray-900 flex items-center">
              <ChartBarIcon className="h-4 w-4 mr-2 text-primary-600" />
              投資ポートフォリオ
            </h3>
          </div>
          {investments.length === 0 ? (
            <div className="p-6 text-center">
              <div className="mx-auto h-12 w-12 bg-gray-100 rounded-full flex items-center justify-center mb-3">
                <ChartBarIcon className="h-6 w-6 text-gray-400" />
              </div>
              <p className="text-gray-500 text-sm">投資情報が見つかりません</p>
            </div>
          ) : (
            <div className="divide-y divide-gray-100">
              {investments.map((investment) => {
                const profit = investment.currentValue - investment.amount;
                const profitRate = (profit / investment.amount) * 100;
                const colorClass = getInvestmentTypeColor(investment.investmentType);
                
                return (
                  <div key={investment.id} className="p-4 hover:bg-gray-50 transition-colors duration-200">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center flex-1">
                        <div className={`h-10 w-10 rounded-lg bg-gradient-to-r ${colorClass} flex items-center justify-center`}>
                          <ChartBarIcon className="h-5 w-5 text-white" />
                        </div>
                        <div className="ml-3 flex-1">
                          <div className="flex items-center">
                            <h4 className="text-base font-semibold text-gray-900">{investment.productName}</h4>
                            <span className={`ml-2 px-1.5 py-0.5 rounded-full text-xs font-medium ${
                              investment.status === 'ACTIVE' 
                                ? 'bg-green-100 text-green-800' 
                                : 'bg-gray-100 text-gray-800'
                            }`}>
                              {investment.status === 'ACTIVE' ? '運用中' : investment.status}
                            </span>
                          </div>
                          <div className="mt-0.5 flex items-center text-xs text-gray-600">
                            <span>{getInvestmentTypeLabel(investment.investmentType)}</span>
                            <span className="mx-1.5">•</span>
                            <span>購入日: {new Date(investment.purchaseDate).toLocaleDateString('ja-JP')}</span>
                          </div>
                        </div>
                      </div>
                      <div className="text-right ml-3">
                        <div className="text-xs text-gray-600 mb-0.5">投資額</div>
                        <div className="text-base font-bold text-gray-900">¥{investment.amount.toLocaleString()}</div>
                        <div className="text-xs text-gray-600 mt-1.5 mb-0.5">現在の価値</div>
                        <div className={`text-base font-bold ${profit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                          ¥{investment.currentValue.toLocaleString()}
                        </div>
                        <div className={`text-xs mt-0.5 ${profit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                          {profit >= 0 ? '+' : ''}¥{profit.toLocaleString()} ({profitRate >= 0 ? '+' : ''}{profitRate.toFixed(2)}%)
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* アクションボタン */}
        <div className="mt-6 flex justify-center gap-3">
          <button 
            onClick={() => navigate('/transfer')}
            className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-5 rounded-md transition-colors duration-200 text-sm"
          >
            新規投資を開始
          </button>
          <button 
            onClick={() => navigate('/dashboard')}
            className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2 px-5 rounded-md transition-colors duration-200 text-sm"
          >
            ダッシュボードに戻る
          </button>
        </div>
      </div>
    </div>
  );
};

export default Investments;
