import React, { useState, useEffect } from 'react';
import { apiService, Account } from '../services/api';
import { 
  BanknotesIcon, 
  CreditCardIcon, 
  ExclamationTriangleIcon,
  CheckCircleIcon,
  ArrowRightIcon,
  ClockIcon,
  ShieldCheckIcon
} from '@heroicons/react/24/outline';

interface TransferForm {
  fromAccountId: number;
  toAccountNumber: string;
  toAccountName: string;
  amount: number;
  description: string;
  transferType: 'IMMEDIATE' | 'SCHEDULED';
  scheduledDate?: string;
}

const Transfer: React.FC = () => {
  const [userId] = useState(1);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [formData, setFormData] = useState<TransferForm>({
    fromAccountId: 0,
    toAccountNumber: '',
    toAccountName: '',
    amount: 0,
    description: '',
    transferType: 'IMMEDIATE'
  });

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        setError(null);
        const accountsData = await apiService.getAccounts(userId);
        setAccounts(accountsData);
        if (accountsData.length > 0) {
          setFormData(prev => ({ ...prev, fromAccountId: accountsData[0].id }));
        }
      } catch (err) {
        console.error('Error fetching accounts:', err);
        setError('口座情報の取得に失敗しました。');
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, [userId]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'amount' ? parseFloat(value) || 0 : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      // 実際のAPI呼び出しをシミュレート
      await new Promise(resolve => setTimeout(resolve, 2000));
      setShowSuccess(true);
    } catch (err) {
      setError('振込処理に失敗しました。');
    } finally {
      setIsSubmitting(false);
    }
  };

  const selectedAccount = accounts.find(acc => acc.id === formData.fromAccountId);
  const transferFee = formData.transferType === 'IMMEDIATE' ? 220 : 110;
  const totalAmount = formData.amount + transferFee;

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

  if (showSuccess) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full mx-4">
          <div className="text-center">
            <div className="mx-auto h-16 w-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
              <CheckCircleIcon className="h-8 w-8 text-green-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">振込が完了しました</h3>
            <p className="text-gray-600 mb-6">振込処理が正常に完了しました</p>
            <button 
              onClick={() => setShowSuccess(false)} 
              className="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
            >
              新しい振込を行う
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* ヘッダーセクション */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">振込</h1>
            <p className="text-gray-600 mt-2">安全で確実な振込サービスをご利用いただけます</p>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* メインフォーム */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-2xl shadow-lg p-8">
              <div className="flex items-center mb-6">
                <div className="h-10 w-10 bg-primary-100 rounded-full flex items-center justify-center mr-3">
                  <BanknotesIcon className="h-5 w-5 text-primary-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900">振込情報入力</h2>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* 振出口座 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振出口座
                  </label>
                  <select
                    name="fromAccountId"
                    value={formData.fromAccountId}
                    onChange={handleInputChange}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    required
                  >
                    {accounts.map((account) => (
                      <option key={account.id} value={account.id}>
                        {account.accountNumber} - ¥{account.balance.toLocaleString()} ({account.accountType})
                      </option>
                    ))}
                  </select>
                </div>

                {/* 振込先口座番号 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振込先口座番号
                  </label>
                  <input
                    type="text"
                    name="toAccountNumber"
                    value={formData.toAccountNumber}
                    onChange={handleInputChange}
                    placeholder="例: 1234-5678-9012"
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    required
                  />
                </div>

                {/* 振込先名義 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振込先名義
                  </label>
                  <input
                    type="text"
                    name="toAccountName"
                    value={formData.toAccountName}
                    onChange={handleInputChange}
                    placeholder="例: 田中太郎"
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    required
                  />
                </div>

                {/* 振込金額 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振込金額
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-3 text-gray-500">¥</span>
                    <input
                      type="number"
                      name="amount"
                      value={formData.amount || ''}
                      onChange={handleInputChange}
                      placeholder="0"
                      min="1"
                      step="1"
                      className="w-full border border-gray-300 rounded-lg pl-8 pr-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                      required
                    />
                  </div>
                </div>

                {/* 振込種別 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振込種別
                  </label>
                  <div className="grid grid-cols-2 gap-4">
                    <label className="flex items-center p-4 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="radio"
                        name="transferType"
                        value="IMMEDIATE"
                        checked={formData.transferType === 'IMMEDIATE'}
                        onChange={handleInputChange}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500"
                      />
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">即時振込</div>
                        <div className="text-xs text-gray-500">手数料: ¥220</div>
                      </div>
                    </label>
                    <label className="flex items-center p-4 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="radio"
                        name="transferType"
                        value="SCHEDULED"
                        checked={formData.transferType === 'SCHEDULED'}
                        onChange={handleInputChange}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500"
                      />
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">指定日振込</div>
                        <div className="text-xs text-gray-500">手数料: ¥110</div>
                      </div>
                    </label>
                  </div>
                </div>

                {/* 指定日（指定日振込の場合） */}
                {formData.transferType === 'SCHEDULED' && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      振込指定日
                    </label>
                    <input
                      type="date"
                      name="scheduledDate"
                      value={formData.scheduledDate || ''}
                      onChange={handleInputChange}
                      min={new Date().toISOString().split('T')[0]}
                      className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                      required
                    />
                  </div>
                )}

                {/* 振込内容 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    振込内容
                  </label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="例: 家賃支払い"
                    rows={3}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                  />
                </div>

                {/* 送信ボタン */}
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full bg-primary-600 hover:bg-primary-700 disabled:bg-primary-400 text-white font-semibold py-4 px-6 rounded-lg transition-colors duration-200 flex items-center justify-center"
                >
                  {isSubmitting ? (
                    <>
                      <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                      処理中...
                    </>
                  ) : (
                    <>
                      振込確認画面へ
                      <ArrowRightIcon className="h-5 w-5 ml-2" />
                    </>
                  )}
                </button>
              </form>
            </div>
          </div>

          {/* サイドバー */}
          <div className="space-y-6">
            {/* 振出口座情報 */}
            {selectedAccount && (
              <div className="bg-white rounded-2xl shadow-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                  <CreditCardIcon className="h-5 w-5 mr-2 text-primary-600" />
                  振出口座情報
                </h3>
                <div className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">口座番号</span>
                    <span className="text-sm font-medium text-gray-900">{selectedAccount.accountNumber}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">口座種別</span>
                    <span className="text-sm font-medium text-gray-900">{selectedAccount.accountType}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">利用可能残高</span>
                    <span className="text-sm font-bold text-gray-900">¥{selectedAccount.balance.toLocaleString()}</span>
                  </div>
                </div>
              </div>
            )}

            {/* 振込手数料 */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <BanknotesIcon className="h-5 w-5 mr-2 text-primary-600" />
                振込手数料
              </h3>
              <div className="space-y-3">
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">振込手数料</span>
                  <span className="text-sm font-medium text-gray-900">¥{transferFee.toLocaleString()}</span>
                </div>
                <div className="border-t border-gray-200 pt-3">
                  <div className="flex justify-between">
                    <span className="text-sm font-medium text-gray-900">合計金額</span>
                    <span className="text-lg font-bold text-primary-600">¥{totalAmount.toLocaleString()}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* セキュリティ情報 */}
            <div className="bg-gradient-to-r from-green-50 to-green-100 rounded-2xl p-6 border border-green-200">
              <div className="flex items-center mb-3">
                <ShieldCheckIcon className="h-5 w-5 text-green-600 mr-2" />
                <h3 className="text-sm font-semibold text-green-800">セキュリティ</h3>
              </div>
              <ul className="text-xs text-green-700 space-y-1">
                <li>• 暗号化通信で保護</li>
                <li>• 二段階認証対応</li>
                <li>• 不正検知システム</li>
                <li>• 24時間監視体制</li>
              </ul>
            </div>

            {/* 振込時間 */}
            <div className="bg-blue-50 rounded-2xl p-6 border border-blue-200">
              <div className="flex items-center mb-3">
                <ClockIcon className="h-5 w-5 text-blue-600 mr-2" />
                <h3 className="text-sm font-semibold text-blue-800">振込時間</h3>
              </div>
              <div className="text-xs text-blue-700 space-y-2">
                <div>
                  <div className="font-medium">即時振込</div>
                  <div>平日 9:00-15:00</div>
                </div>
                <div>
                  <div className="font-medium">指定日振込</div>
                  <div>前営業日まで</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Transfer;
