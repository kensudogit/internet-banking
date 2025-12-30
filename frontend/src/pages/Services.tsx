import React from 'react';
import { 
  CreditCardIcon, 
  BanknotesIcon, 
  BuildingOfficeIcon, 
  ChartBarIcon,
  ShieldCheckIcon,
  GlobeAltIcon,
  PhoneIcon,
  ChatBubbleLeftRightIcon
} from '@heroicons/react/24/outline';

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
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* ヘッダーセクション */}
      <div className="relative overflow-hidden bg-white">
        <div className="absolute inset-0 bg-gradient-to-r from-primary-600 to-primary-800 opacity-10"></div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl md:text-6xl">
              豊富なサービスで
              <span className="text-primary-600">お客様の暮らし</span>
              をサポート
            </h1>
            <p className="mt-6 text-xl text-gray-600 max-w-3xl mx-auto">
              インターネットバンキングを通じて、いつでもどこでも便利に利用できる
              多様な金融サービスをご提供しています
            </p>
            <div className="mt-10 flex justify-center">
              <div className="rounded-full bg-primary-100 p-1">
                <div className="rounded-full bg-primary-600 px-8 py-3 text-white font-semibold">
                  サービス一覧を見る
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* サービス一覧 */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center mb-16">
          <h2 className="text-3xl font-bold text-gray-900 sm:text-4xl">
            主要サービス
          </h2>
          <p className="mt-4 text-lg text-gray-600">
            お客様のニーズに合わせて選べる豊富なサービスラインナップ
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {services.map((service) => (
            <div
              key={service.id}
              className="group relative bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 overflow-hidden"
            >
              {/* カードヘッダー */}
              <div className={`bg-gradient-to-r ${service.color} p-6 text-white`}>
                <div className="flex items-center justify-between">
                  <service.icon className="h-12 w-12 text-white" />
                  {service.isPopular && (
                    <span className="bg-yellow-400 text-yellow-900 px-3 py-1 rounded-full text-sm font-semibold">
                      人気
                    </span>
                  )}
                </div>
                <h3 className="mt-4 text-xl font-bold">{service.title}</h3>
                <p className="mt-2 text-primary-100 text-sm">{service.description}</p>
              </div>

              {/* カードボディ */}
              <div className="p-6">
                <div className="space-y-3">
                  {service.features.map((feature, index) => (
                    <div key={index} className="flex items-center">
                      <div className="flex-shrink-0">
                        <div className="h-2 w-2 bg-primary-500 rounded-full"></div>
                      </div>
                      <span className="ml-3 text-sm text-gray-600">{feature}</span>
                    </div>
                  ))}
                </div>
                
                <div className="mt-6">
                  <button className="w-full bg-gray-100 hover:bg-primary-50 text-gray-700 hover:text-primary-700 font-semibold py-3 px-4 rounded-lg transition-colors duration-200 group-hover:bg-primary-50 group-hover:text-primary-700">
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
      <div className="bg-white py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-gray-900 sm:text-4xl">
              なぜ選ばれるのか
            </h2>
            <p className="mt-4 text-lg text-gray-600">
              インターネットバンキングの特徴と魅力
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="mx-auto h-16 w-16 bg-primary-100 rounded-full flex items-center justify-center mb-4">
                <ShieldCheckIcon className="h-8 w-8 text-primary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">最高水準のセキュリティ</h3>
              <p className="text-gray-600">最新の暗号化技術と不正検知システムで、お客様の資産を安全に保護します</p>
            </div>
            
            <div className="text-center">
              <div className="mx-auto h-16 w-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                <GlobeAltIcon className="h-8 w-8 text-green-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">24時間いつでも利用可能</h3>
              <p className="text-gray-600">時間や場所を問わず、いつでもどこでも便利に利用できます</p>
            </div>
            
            <div className="text-center">
              <div className="mx-auto h-16 w-16 bg-purple-100 rounded-full flex items-center justify-center mb-4">
                <ChartBarIcon className="h-8 w-8 text-purple-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">豊富なサービス</h3>
              <p className="text-gray-600">預金から投資まで、幅広い金融サービスをワンストップで提供</p>
            </div>
          </div>
        </div>
      </div>

      {/* お問い合わせセクション */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-white sm:text-4xl">
              お困りの際はお気軽に
            </h2>
            <p className="mt-4 text-xl text-primary-100">
              専門スタッフが丁寧にサポートいたします
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {contactMethods.map((method, index) => (
              <div key={index} className="bg-white rounded-2xl p-8 shadow-lg">
                <div className="flex items-center mb-4">
                  <div className="h-12 w-12 bg-primary-100 rounded-full flex items-center justify-center">
                    <method.icon className="h-6 w-6 text-primary-600" />
                  </div>
                  <div className="ml-4">
                    <h3 className="text-xl font-semibold text-gray-900">{method.name}</h3>
                    <p className="text-sm text-gray-500">{method.available}</p>
                  </div>
                </div>
                <p className="text-gray-600 mb-4">{method.description}</p>
                <button className="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200">
                  {method.contact}
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* CTAセクション */}
      <div className="bg-white py-16">
        <div className="max-w-4xl mx-auto text-center px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-gray-900 sm:text-4xl">
            今すぐ始めませんか？
          </h2>
          <p className="mt-4 text-lg text-gray-600">
            インターネットバンキングの便利さを体験してください
          </p>
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <button className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-4 px-8 rounded-lg transition-colors duration-200 text-lg">
              口座を開設する
            </button>
            <button className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-4 px-8 rounded-lg transition-colors duration-200 text-lg">
              デモを体験する
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Services;
