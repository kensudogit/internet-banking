import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  CreditCardIcon, 
  BanknotesIcon, 
  BuildingOfficeIcon, 
  ChartBarIcon,
  ShieldCheckIcon,
  GlobeAltIcon,
  PhoneIcon,
  ChatBubbleLeftRightIcon,
  XMarkIcon,
  CheckCircleIcon
} from '@heroicons/react/24/outline';
import { apiService } from '../services/api';

interface ServiceCard {
  id: number;
  title: string;
  description: string;
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
  color: string;
  features: string[];
  isPopular?: boolean;
}

const services: ServiceCard[] = [
  {
    id: 1,
    title: '口座管理',
    description: '安全で使いやすい口座管理サービスで、残高照会から取引履歴まで一元管理',
    icon: BanknotesIcon,
    color: 'from-blue-500 to-blue-600',
    features: ['残高照会', '取引履歴', '口座開設', '口座変更']
  },
  {
    id: 2,
    title: '振込・送金',
    description: '24時間いつでも、どこからでも安全に振込・送金ができるサービス',
    icon: CreditCardIcon,
    color: 'from-green-500 to-green-600',
    features: ['即時振込', '定額振込', '手数料優遇', '送金上限設定']
  },
  {
    id: 3,
    title: '投資・資産運用',
    description: '豊富な商品ラインナップで、お客様の資産形成をサポート',
    icon: ChartBarIcon,
    color: 'from-purple-500 to-purple-600',
    features: ['投資信託', '株式', '債券', '保険商品']
  },
  {
    id: 4,
    title: 'ローン・融資',
    description: '住宅ローンからカードローンまで、お客様のライフスタイルに合わせた融資サービス',
    icon: BuildingOfficeIcon,
    color: 'from-orange-500 to-orange-600',
    features: ['住宅ローン', 'カードローン', '教育ローン', '事業者ローン']
  },
  {
    id: 5,
    title: 'セキュリティ',
    description: '最新のセキュリティ技術で、お客様の資産と情報を守ります',
    icon: ShieldCheckIcon,
    color: 'from-red-500 to-red-600',
    features: ['二段階認証', '不正検知', '暗号化通信', 'セキュリティ監視']
  },
  {
    id: 6,
    title: '国際サービス',
    description: 'グローバルな取引をサポートする国際送金・為替サービス',
    icon: GlobeAltIcon,
    color: 'from-indigo-500 to-indigo-600',
    features: ['国際送金', '為替取引', '外貨預金', '海外ATM利用']
  }
];

const contactMethods = [
  {
    name: 'カスタマーサポート',
    description: '24時間365日、お客様のご質問にお答えします',
    icon: PhoneIcon,
    contact: '0120-XXX-XXX',
    available: '24時間対応'
  },
  {
    name: 'オンラインチャット',
    description: 'リアルタイムでサポートスタッフとチャットできます',
    icon: ChatBubbleLeftRightIcon,
    contact: 'チャット開始',
    available: '平日 9:00-18:00'
  }
];

const Services: React.FC = () => {
  const navigate = useNavigate();
  const [showAccountModal, setShowAccountModal] = useState(false);
  const [showDemoModal, setShowDemoModal] = useState(false);
  const [accountType, setAccountType] = useState('SAVINGS');
  const [isCreating, setIsCreating] = useState(false);
  const [accountCreated, setAccountCreated] = useState(false);
  const [createdAccount, setCreatedAccount] = useState<any>(null);

  const handleOpenAccount = async () => {
    setIsCreating(true);
    setAccountCreated(false);
    
    try {
      // サンプルデータで口座を作成（ユーザーID 1を使用）
      const accountTypes = ['SAVINGS', 'CHECKING', 'FIXED_DEPOSIT'];
      const randomType = accountTypes[Math.floor(Math.random() * accountTypes.length)];
      const interestRates = {
        'SAVINGS': 0.0010,
        'CHECKING': 0.0005,
        'FIXED_DEPOSIT': 0.0050
      };

      // 実際のAPIを呼び出すか、サンプルデータを返す
      let newAccount;
      try {
        newAccount = await apiService.createAccount(1, randomType, 'JPY', interestRates[randomType as keyof typeof interestRates]);
      } catch (error) {
        // APIが失敗した場合はサンプルデータを使用
        const accountNumbers = [
          '1234-5678-9012',
          '8765-4321-0987',
          '1111-2222-3333',
          '9999-8888-7777',
          '5555-6666-7777'
        ];
        const randomNumber = accountNumbers[Math.floor(Math.random() * accountNumbers.length)];
        
        newAccount = {
          id: Date.now(),
          userId: 1,
          accountNumber: randomNumber,
          accountType: randomType,
          balance: 0,
          currency: 'JPY',
          status: 'ACTIVE',
          interestRate: interestRates[randomType as keyof typeof interestRates]
        };
      }

      setCreatedAccount(newAccount);
      setAccountCreated(true);
    } catch (error) {
      console.error('Error creating account:', error);
    } finally {
      setIsCreating(false);
    }
  };

  const handleDemoExperience = () => {
    // デモモードとしてダッシュボードに遷移（ユーザーID 1のデータを表示）
    localStorage.setItem('demoMode', 'true');
    navigate('/dashboard');
  };

  const handleCloseAccountModal = () => {
    setShowAccountModal(false);
    setAccountCreated(false);
    setCreatedAccount(null);
  };

  const getAccountTypeLabel = (type: string) => {
    switch (type) {
      case 'SAVINGS':
        return '普通預金';
      case 'CHECKING':
        return '当座預金';
      case 'FIXED_DEPOSIT':
        return '定期預金';
      default:
        return type;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* ヘッダーセクション */}
      <div className="relative overflow-hidden bg-white">
        <div className="absolute inset-0 bg-gradient-to-r from-primary-600 to-primary-800 opacity-10"></div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 sm:text-3xl md:text-4xl">
              豊富なサービスで
              <span className="text-primary-600">お客様の暮らし</span>
              をサポート
            </h1>
            <p className="mt-2 text-sm text-gray-600 max-w-3xl mx-auto">
              インターネットバンキングを通じて、いつでもどこでも便利に利用できる
              多様な金融サービスをご提供しています
            </p>
            <div className="mt-4 flex justify-center">
              <div className="rounded-full bg-primary-100 p-0.5">
                <div className="rounded-full bg-primary-600 px-4 py-1.5 text-white font-medium text-xs">
                  サービス一覧を見る
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* サービス一覧 */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="text-center mb-6">
          <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">
            主要サービス
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            お客様のニーズに合わせて選べる豊富なサービスラインナップ
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {services.map((service) => (
            <div
              key={service.id}
              className="group relative bg-white rounded-lg shadow-md hover:shadow-lg transition-all duration-300 transform hover:-translate-y-0.5 overflow-hidden"
            >
              {/* カードヘッダー */}
              <div className={`bg-gradient-to-r ${service.color} p-2.5 text-white`}>
                <div className="flex items-center justify-between">
                  <service.icon className="h-6 w-6 text-white" />
                  {service.isPopular && (
                    <span className="bg-yellow-400 text-yellow-900 px-1.5 py-0.5 rounded-full text-xs font-semibold">
                      人気
                    </span>
                  )}
                </div>
                <h3 className="mt-1.5 text-sm font-bold">{service.title}</h3>
                <p className="mt-0.5 text-primary-100 text-xs leading-tight">{service.description}</p>
              </div>

              {/* カードボディ */}
              <div className="p-3">
                <div className="space-y-1.5">
                  {service.features.map((feature, index) => (
                    <div key={index} className="flex items-center">
                      <div className="flex-shrink-0">
                        <div className="h-1 w-1 bg-primary-500 rounded-full"></div>
                      </div>
                      <span className="ml-1.5 text-xs text-gray-600">{feature}</span>
                    </div>
                  ))}
                </div>
                
                <div className="mt-3">
                  <button 
                    onClick={() => {
                      // サービスに応じて適切なページに遷移
                      switch(service.id) {
                        case 1: // 口座管理
                          navigate('/accounts');
                          break;
                        case 2: // 振込・送金
                          navigate('/transfer');
                          break;
                        case 3: // 投資・資産運用
                          navigate('/investments');
                          break;
                        case 4: // ローン・融資
                          // ローン専用ページがない場合はサービスページのまま
                          break;
                        case 5: // セキュリティ
                          // セキュリティ専用ページがない場合はサービスページのまま
                          break;
                        case 6: // 国際サービス
                          // 国際サービス専用ページがない場合はサービスページのまま
                          break;
                        default:
                          break;
                      }
                    }}
                    className="w-full bg-gray-100 hover:bg-primary-50 text-gray-700 hover:text-primary-700 font-medium py-1.5 px-2.5 rounded-md transition-colors duration-200 group-hover:bg-primary-50 group-hover:text-primary-700 text-xs"
                  >
                    詳細を見る
                  </button>
                </div>
              </div>

              {/* ホバーエフェクト */}
              <div className="absolute inset-0 bg-gradient-to-r from-primary-600 to-primary-800 opacity-0 group-hover:opacity-10 transition-opacity duration-300 rounded-2xl"></div>
            </div>
          ))}
        </div>
      </div>

      {/* 特徴セクション */}
      <div className="bg-white py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-6">
            <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">
              なぜ選ばれるのか
            </h2>
            <p className="mt-2 text-sm text-gray-600">
              インターネットバンキングの特徴と魅力
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="text-center">
              <div className="mx-auto h-10 w-10 bg-primary-100 rounded-full flex items-center justify-center mb-2">
                <ShieldCheckIcon className="h-5 w-5 text-primary-600" />
              </div>
              <h3 className="text-base font-semibold text-gray-900 mb-1.5">最高水準のセキュリティ</h3>
              <p className="text-xs text-gray-600">最新の暗号化技術と不正検知システムで、お客様の資産を安全に保護します</p>
            </div>
            
            <div className="text-center">
              <div className="mx-auto h-10 w-10 bg-green-100 rounded-full flex items-center justify-center mb-2">
                <GlobeAltIcon className="h-5 w-5 text-green-600" />
              </div>
              <h3 className="text-base font-semibold text-gray-900 mb-1.5">24時間いつでも利用可能</h3>
              <p className="text-xs text-gray-600">時間や場所を問わず、いつでもどこでも便利に利用できます</p>
            </div>
            
            <div className="text-center">
              <div className="mx-auto h-10 w-10 bg-purple-100 rounded-full flex items-center justify-center mb-2">
                <ChartBarIcon className="h-5 w-5 text-purple-600" />
              </div>
              <h3 className="text-base font-semibold text-gray-900 mb-1.5">豊富なサービス</h3>
              <p className="text-xs text-gray-600">預金から投資まで、幅広い金融サービスをワンストップで提供</p>
            </div>
          </div>
        </div>
      </div>

      {/* お問い合わせセクション */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-6">
            <h2 className="text-xl font-bold text-white sm:text-2xl">
              お困りの際はお気軽に
            </h2>
            <p className="mt-2 text-sm text-primary-100">
              専門スタッフが丁寧にサポートいたします
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {contactMethods.map((method, index) => (
              <div key={index} className="bg-white rounded-lg p-4 shadow-lg">
                <div className="flex items-center mb-2">
                  <div className="h-8 w-8 bg-primary-100 rounded-full flex items-center justify-center">
                    <method.icon className="h-4 w-4 text-primary-600" />
                  </div>
                  <div className="ml-2.5">
                    <h3 className="text-base font-semibold text-gray-900">{method.name}</h3>
                    <p className="text-xs text-gray-500">{method.available}</p>
                  </div>
                </div>
                <p className="text-xs text-gray-600 mb-2.5">{method.description}</p>
                <button className="w-full bg-primary-600 hover:bg-primary-700 text-white font-medium py-1.5 px-2.5 rounded-md transition-colors duration-200 text-xs">
                  {method.contact}
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* CTAセクション */}
      <div className="bg-white py-6">
        <div className="max-w-4xl mx-auto text-center px-4 sm:px-6 lg:px-8">
          <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">
            今すぐ始めませんか？
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            インターネットバンキングの便利さを体験してください
          </p>
          <div className="mt-4 flex flex-col sm:flex-row gap-2 justify-center">
            <button 
              onClick={() => setShowAccountModal(true)}
              className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-5 rounded-md transition-colors duration-200 text-sm"
            >
              口座を開設する
            </button>
            <button 
              onClick={handleDemoExperience}
              className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2 px-5 rounded-md transition-colors duration-200 text-sm"
            >
              デモを体験する
            </button>
          </div>
        </div>
      </div>

      {/* 口座開設モーダル */}
      {showAccountModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="p-4 border-b border-gray-200 flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">口座を開設する</h3>
              <button
                onClick={handleCloseAccountModal}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <XMarkIcon className="h-5 w-5" />
              </button>
            </div>

            <div className="p-4">
              {!accountCreated ? (
                <>
                  <p className="text-sm text-gray-600 mb-4">
                    口座タイプを選択してください。サンプルデータを使用して口座を作成します。
                  </p>
                  
                  <div className="space-y-3 mb-4">
                    <label className="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="radio"
                        name="accountType"
                        value="SAVINGS"
                        checked={accountType === 'SAVINGS'}
                        onChange={(e) => setAccountType(e.target.value)}
                        className="mr-3"
                      />
                      <div>
                        <div className="font-medium text-sm text-gray-900">普通預金</div>
                        <div className="text-xs text-gray-500">金利: 0.10%</div>
                      </div>
                    </label>

                    <label className="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="radio"
                        name="accountType"
                        value="CHECKING"
                        checked={accountType === 'CHECKING'}
                        onChange={(e) => setAccountType(e.target.value)}
                        className="mr-3"
                      />
                      <div>
                        <div className="font-medium text-sm text-gray-900">当座預金</div>
                        <div className="text-xs text-gray-500">金利: 0.05%</div>
                      </div>
                    </label>

                    <label className="flex items-center p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="radio"
                        name="accountType"
                        value="FIXED_DEPOSIT"
                        checked={accountType === 'FIXED_DEPOSIT'}
                        onChange={(e) => setAccountType(e.target.value)}
                        className="mr-3"
                      />
                      <div>
                        <div className="font-medium text-sm text-gray-900">定期預金</div>
                        <div className="text-xs text-gray-500">金利: 0.50%</div>
                      </div>
                    </label>
                  </div>

                  <button
                    onClick={handleOpenAccount}
                    disabled={isCreating}
                    className="w-full bg-primary-600 hover:bg-primary-700 disabled:bg-gray-400 text-white font-semibold py-2 px-4 rounded-md transition-colors duration-200 text-sm"
                  >
                    {isCreating ? '作成中...' : '口座を開設する'}
                  </button>
                </>
              ) : (
                <div className="text-center py-4">
                  <CheckCircleIcon className="h-16 w-16 text-green-500 mx-auto mb-4" />
                  <h4 className="text-lg font-semibold text-gray-900 mb-2">口座開設が完了しました！</h4>
                  <div className="bg-gray-50 rounded-lg p-4 mb-4 text-left">
                    <div className="text-sm text-gray-600 mb-1">口座タイプ</div>
                    <div className="font-semibold text-gray-900 mb-3">{getAccountTypeLabel(createdAccount?.accountType || accountType)}</div>
                    <div className="text-sm text-gray-600 mb-1">口座番号</div>
                    <div className="font-semibold text-gray-900 mb-3">{createdAccount?.accountNumber || '---'}</div>
                    <div className="text-sm text-gray-600 mb-1">初期残高</div>
                    <div className="font-semibold text-gray-900">¥{createdAccount?.balance?.toLocaleString() || '0'}</div>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => {
                        handleCloseAccountModal();
                        navigate('/accounts');
                      }}
                      className="flex-1 bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-md transition-colors duration-200 text-sm"
                    >
                      口座一覧を見る
                    </button>
                    <button
                      onClick={handleCloseAccountModal}
                      className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2 px-4 rounded-md transition-colors duration-200 text-sm"
                    >
                      閉じる
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Services;
