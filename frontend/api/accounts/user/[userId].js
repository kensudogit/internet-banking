// サンプル口座データ
const accounts = [
  {
    id: 1,
    userId: 1,
    accountNumber: '1234-5678-9012',
    accountType: 'SAVINGS',
    balance: 500000.00,
    currency: 'JPY',
    status: 'ACTIVE',
    interestRate: 0.0010
  },
  {
    id: 2,
    userId: 1,
    accountNumber: '8765-4321-0987',
    accountType: 'FIXED_DEPOSIT',
    balance: 1000000.00,
    currency: 'JPY',
    status: 'ACTIVE',
    interestRate: 0.0050
  },
  {
    id: 3,
    userId: 2,
    accountNumber: '1111-2222-3333',
    accountType: 'CHECKING',
    balance: 2500000.00,
    currency: 'JPY',
    status: 'ACTIVE',
    interestRate: 0.0005
  }
];

export default async function handler(req, res) {
  console.log('Accounts API called:', { method: req.method, url: req.url, query: req.query });
  
  if (req.method !== 'GET') {
    return res.status(405).json({ message: 'Method not allowed' });
  }

  try {
    const { userId } = req.query;
    console.log('Requested userId:', userId);

    if (!userId) {
      return res.status(400).json({ message: 'ユーザーIDが必要です' });
    }

    // ユーザーの口座を検索
    const userAccounts = accounts.filter(account => account.userId === parseInt(userId));
    console.log('Found accounts:', userAccounts.length);

    if (userAccounts.length === 0) {
      return res.status(200).json([]);
    }

    return res.status(200).json(userAccounts);

  } catch (error) {
    console.error('Get accounts error:', error);
    return res.status(500).json({ message: '内部サーバーエラーが発生しました' });
  }
}
