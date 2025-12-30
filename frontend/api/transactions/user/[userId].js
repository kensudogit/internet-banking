// サンプル取引履歴データ
const transactions = [
  {
    id: 1,
    fromAccountId: 1,
    toAccountId: 2,
    transactionType: 'TRANSFER',
    amount: 50000.00,
    currency: 'JPY',
    description: '給料振込',
    status: 'COMPLETED',
    referenceNumber: 'TXN001',
    transactionDate: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString() // 1日前
  },
  {
    id: 2,
    fromAccountId: 2,
    toAccountId: 1,
    transactionType: 'TRANSFER',
    amount: 25000.00,
    currency: 'JPY',
    description: '家賃支払い',
    status: 'COMPLETED',
    referenceNumber: 'TXN002',
    transactionDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString() // 2日前
  },
  {
    id: 3,
    fromAccountId: 1,
    toAccountId: null,
    transactionType: 'DEPOSIT',
    amount: 100000.00,
    currency: 'JPY',
    description: '現金入金',
    status: 'COMPLETED',
    referenceNumber: 'TXN003',
    transactionDate: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString() // 3日前
  }
];

export default async function handler(req, res) {
  console.log('Transactions API called:', { method: req.method, url: req.url, query: req.query });
  
  if (req.method !== 'GET') {
    return res.status(405).json({ message: 'Method not allowed' });
  }

  try {
    const { userId } = req.query;
    console.log('Requested userId:', userId);

    if (!userId) {
      return res.status(400).json({ message: 'ユーザーIDが必要です' });
    }

    // ユーザーの口座IDを取得（実際の実装ではデータベースから取得）
    const userAccountIds = [1, 2]; // 仮の値
    console.log('User account IDs:', userAccountIds);

    // ユーザーの口座に関連する取引履歴を検索
    const userTransactions = transactions.filter(transaction => 
      userAccountIds.includes(transaction.fromAccountId) || 
      (transaction.toAccountId !== null && userAccountIds.includes(transaction.toAccountId))
    );
    console.log('Found transactions:', userTransactions.length);

    // 取引履歴を日付順でソート（新しい順）
    userTransactions.sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate));

    return res.status(200).json(userTransactions);

  } catch (error) {
    console.error('Get transactions error:', error);
    return res.status(500).json({ message: '内部サーバーエラーが発生しました' });
  }
}
