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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">口座情報</h1>
              <p className="text-gray-600 mt-2">お客様の口座一覧と詳細情報をご確認いただけます</p>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setShowBalance(!showBalance)}
                className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg transition-colors duration-200 flex items-center"
              >
                {showBalance ? (
                  <>
                    <EyeSlashIcon className="h-4 w-4 mr-2" />
                    残高を隠す
                  </>
                ) : (
                  <>
                    <EyeIcon className="h-4 w-4 mr-2" />
                    残高を表示
                  </>
                )}
              </button>
              <button className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center">
                <PlusIcon className="h-4 w-4 mr-2" />
                新規口座開設
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 総残高サマリー */}
        <div className="bg-gradient-to-r from-primary-600 to-primary-700 rounded-2xl shadow-lg p-8 text-white mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-semibold mb-2">総残高</h2>
              <div className="text-4xl font-bold">
                {showBalance ? `¥${totalBalance.toLocaleString()}` : '¥***,***,***'}
              </div>
              <p className="text-primary-100 mt-2">保有口座数: {accounts.length}件</p>
            </div>
            <div className="h-20 w-20 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
              <BanknotesIcon className="h-10 w-10 text-white" />
            </div>
          </div>
        </div>

        {/* 口座一覧 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {accounts.map((account) => {
            const IconComponent = getAccountTypeIcon(account.accountType);
            const colorClass = getAccountTypeColor(account.accountType);
            
            return (
              <div
                key={account.id}
                className="bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300"
              >
                {/* カードヘッダー */}
                <div className={`bg-gradient-to-r ${colorClass} p-6 text-white`}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="h-12 w-12 bg-white bg-opacity-20 rounded-xl flex items-center justify-center">
                        <IconComponent className="h-6 w-6 text-white" />
                      </div>
                      <div className="ml-4">
                        <h3 className="text-lg font-semibold">{getAccountTypeLabel(account.accountType)}</h3>
                        <p className="text-primary-100 text-sm">{account.accountNumber}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm text-primary-100">金利</div>
                      <div className="text-lg font-bold">{(account.interestRate * 100).toFixed(2)}%</div>
                    </div>
                  </div>
                </div>

                {/* カードボディ */}
                <div className="p-6">
                  <div className="space-y-4">
                    <div className="flex justify-between items-center">
                      <span className="text-gray-600">残高</span>
                      <span className="text-2xl font-bold text-gray-900">
                        {showBalance ? `¥${account.balance.toLocaleString()}` : '¥***,***,***'}
                      </span>
                    </div>
                    
                    <div className="flex justify-between items-center">
                      <span className="text-gray-600">通貨</span>
                      <span className="text-gray-900 font-medium">{account.currency}</span>
                    </div>
                    
                    <div className="flex justify-between items-center">
                      <span className="text-gray-600">ステータス</span>
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        account.status === 'ACTIVE' 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {account.status === 'ACTIVE' ? 'アクティブ' : '非アクティブ'}
                      </span>
                    </div>
                  </div>

                  {/* アクションボタン */}
                  <div className="mt-6 grid grid-cols-2 gap-3">
                    <button className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-sm">
                      詳細を見る
                    </button>
                    <button className="bg-primary-50 hover:bg-primary-100 text-primary-700 font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-sm">
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
          <div className="bg-white rounded-2xl shadow-lg p-12 text-center">
            <div className="mx-auto h-24 w-24 bg-gray-100 rounded-full flex items-center justify-center mb-6">
              <BanknotesIcon className="h-12 w-12 text-gray-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">口座がありません</h3>
            <p className="text-gray-600 mb-6">新しい口座を開設して、インターネットバンキングを始めましょう</p>
            <button className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200">
              口座を開設する
            </button>
          </div>
        )}

        {/* 口座開設のメリット */}
        {accounts.length > 0 && (
          <div className="mt-8 bg-white rounded-2xl shadow-lg p-8">
            <h3 className="text-xl font-semibold text-gray-900 mb-6">口座開設のメリット</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="text-center">
                <div className="mx-auto h-16 w-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
                  <BanknotesIcon className="h-8 w-8 text-blue-600" />
                </div>
                <h4 className="text-lg font-semibold text-gray-900 mb-2">高金利</h4>
                <p className="text-gray-600">通常の預金より高い金利で資産を増やせます</p>
              </div>
              
              <div className="text-center">
                <div className="mx-auto h-16 w-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                  <CreditCardIcon className="h-8 w-8 text-green-600" />
                </div>
                <h4 className="text-lg font-semibold text-gray-900 mb-2">便利なサービス</h4>
                <p className="text-gray-600">振込や投資など、豊富なサービスを利用できます</p>
              </div>
              
              <div className="text-center">
                <div className="mx-auto h-16 w-16 bg-purple-100 rounded-full flex items-center justify-center mb-4">
                  <ChartBarIcon className="h-8 w-8 text-purple-600" />
                </div>
                <h4 className="text-lg font-semibold text-gray-900 mb-2">資産管理</h4>
                <p className="text-gray-600">一元管理で資産状況を把握しやすくなります</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Accounts;
