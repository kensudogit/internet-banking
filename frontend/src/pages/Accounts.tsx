import React, { useState, useEffect } from 'react';
import { apiService, Account } from '../services/api';
import { 
  BanknotesIcon, 
  PlusIcon, 
  EyeIcon, 
  EyeSlashIcon,
  CreditCardIcon,
  BuildingOfficeIcon,
  ChartBarIcon,
  ExclamationTriangleIcon
} from '@heroicons/react/24/outline';

const Accounts: React.FC = () => {
  const [userId] = useState(1);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showBalance, setShowBalance] = useState(true);

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        setError(null);
        const accountsData = await apiService.getAccounts(userId);
        setAccounts(accountsData);
      } catch (err) {
        console.error('Error fetching accounts:', err);
        setError('口座情報の取得に失敗しました。');
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, [userId]);

  const getAccountTypeIcon = (accountType: string) => {
    switch (accountType) {
      case 'SAVINGS':
        return BanknotesIcon;
      case 'CHECKING':
        return CreditCardIcon;
      case 'FIXED_DEPOSIT':
        return BuildingOfficeIcon;
      case 'INVESTMENT':
        return ChartBarIcon;
      default:
        return BanknotesIcon;
    }
  };

  const getAccountTypeColor = (accountType: string) => {
    switch (accountType) {
      case 'SAVINGS':
        return 'from-blue-500 to-blue-600';
      case 'CHECKING':
        return 'from-green-500 to-green-600';
      case 'FIXED_DEPOSIT':
        return 'from-purple-500 to-purple-600';
      case 'INVESTMENT':
        return 'from-orange-500 to-orange-600';
      default:
        return 'from-gray-500 to-gray-600';
    }
  };

  const getAccountTypeLabel = (accountType: string) => {
    switch (accountType) {
      case 'SAVINGS':
        return '普通預金';
      case 'CHECKING':
        return '当座預金';
      case 'FIXED_DEPOSIT':
        return '定期預金';
      case 'INVESTMENT':
        return '投資口座';
      default:
        return accountType;
    }
  };

  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);

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
          <div className="text-gray-500">口座情報を取得しています</div>
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
              <h1 className="text-2xl font-bold text-gray-900">口座情報</h1>
              <p className="text-gray-600 mt-1 text-sm">お客様の口座一覧と詳細情報をご確認いただけます</p>
            </div>
            <div className="flex items-center space-x-2">
              <button
                onClick={() => setShowBalance(!showBalance)}
                className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1.5 rounded-md transition-colors duration-200 flex items-center text-sm"
              >
                {showBalance ? (
                  <>
                    <EyeSlashIcon className="h-3.5 w-3.5 mr-1.5" />
                    残高を隠す
                  </>
                ) : (
                  <>
                    <EyeIcon className="h-3.5 w-3.5 mr-1.5" />
                    残高を表示
                  </>
                )}
              </button>
              <button className="bg-primary-600 hover:bg-primary-700 text-white px-3 py-1.5 rounded-md transition-colors duration-200 flex items-center text-sm">
                <PlusIcon className="h-3.5 w-3.5 mr-1.5" />
                新規口座開設
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        {/* 総残高サマリー */}
        <div className="bg-gradient-to-r from-primary-600 to-primary-700 rounded-xl shadow-lg p-4 text-white mb-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold mb-0.5">総残高</h2>
              <div className="text-2xl font-bold">
                {showBalance ? `¥${totalBalance.toLocaleString()}` : '¥***,***,***'}
              </div>
              <p className="text-primary-100 mt-0.5 text-xs">保有口座数: {accounts.length}件</p>
            </div>
            <div className="h-12 w-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
              <BanknotesIcon className="h-6 w-6 text-white" />
            </div>
          </div>
        </div>

        {/* 口座一覧 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          {accounts.map((account) => {
            const IconComponent = getAccountTypeIcon(account.accountType);
            const colorClass = getAccountTypeColor(account.accountType);
            
            return (
              <div
                key={account.id}
                className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300"
              >
                {/* カードヘッダー */}
                <div className={`bg-gradient-to-r ${colorClass} p-4 text-white`}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="h-10 w-10 bg-white bg-opacity-20 rounded-lg flex items-center justify-center">
                        <IconComponent className="h-5 w-5 text-white" />
                      </div>
                      <div className="ml-3">
                        <h3 className="text-base font-semibold">{getAccountTypeLabel(account.accountType)}</h3>
                        <p className="text-primary-100 text-xs">{account.accountNumber}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-xs text-primary-100">金利</div>
                      <div className="text-base font-bold">{(account.interestRate * 100).toFixed(2)}%</div>
                    </div>
                  </div>
                </div>

                {/* カードボディ */}
                <div className="p-4">
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-xs text-gray-600">残高</span>
                      <span className="text-xl font-bold text-gray-900">
                        {showBalance ? `¥${account.balance.toLocaleString()}` : '¥***,***,***'}
                      </span>
                    </div>
                    
                    <div className="flex justify-between items-center">
                      <span className="text-xs text-gray-600">通貨</span>
                      <span className="text-sm text-gray-900 font-medium">{account.currency}</span>
                    </div>
                    
                    <div className="flex justify-between items-center">
                      <span className="text-xs text-gray-600">ステータス</span>
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                        account.status === 'ACTIVE' 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {account.status === 'ACTIVE' ? 'アクティブ' : '非アクティブ'}
                      </span>
                    </div>
                  </div>

                  {/* アクションボタン */}
                  <div className="mt-4 grid grid-cols-2 gap-2">
                    <button className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-1.5 px-3 rounded-md transition-colors duration-200 text-xs">
                      詳細を見る
                    </button>
                    <button className="bg-primary-50 hover:bg-primary-100 text-primary-700 font-medium py-1.5 px-3 rounded-md transition-colors duration-200 text-xs">
                      取引履歴
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* 空の状態 */}
        {accounts.length === 0 && (
          <div className="bg-white rounded-xl shadow-lg p-8 text-center">
            <div className="mx-auto h-16 w-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <BanknotesIcon className="h-8 w-8 text-gray-400" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-1.5">口座がありません</h3>
            <p className="text-sm text-gray-600 mb-4">新しい口座を開設して、インターネットバンキングを始めましょう</p>
            <button className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-5 rounded-md transition-colors duration-200 text-sm">
              口座を開設する
            </button>
          </div>
        )}

        {/* 口座開設のメリット */}
        {accounts.length > 0 && (
          <div className="mt-6 bg-white rounded-xl shadow-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">口座開設のメリット</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center">
                <div className="mx-auto h-12 w-12 bg-blue-100 rounded-full flex items-center justify-center mb-3">
                  <BanknotesIcon className="h-6 w-6 text-blue-600" />
                </div>
                <h4 className="text-base font-semibold text-gray-900 mb-1.5">高金利</h4>
                <p className="text-xs text-gray-600">通常の預金より高い金利で資産を増やせます</p>
              </div>
              
              <div className="text-center">
                <div className="mx-auto h-12 w-12 bg-green-100 rounded-full flex items-center justify-center mb-3">
                  <CreditCardIcon className="h-6 w-6 text-green-600" />
                </div>
                <h4 className="text-base font-semibold text-gray-900 mb-1.5">便利なサービス</h4>
                <p className="text-xs text-gray-600">振込や投資など、豊富なサービスを利用できます</p>
              </div>
              
              <div className="text-center">
                <div className="mx-auto h-12 w-12 bg-purple-100 rounded-full flex items-center justify-center mb-3">
                  <ChartBarIcon className="h-6 w-6 text-purple-600" />
                </div>
                <h4 className="text-base font-semibold text-gray-900 mb-1.5">資産管理</h4>
                <p className="text-xs text-gray-600">一元管理で資産状況を把握しやすくなります</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Accounts;
