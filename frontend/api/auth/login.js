const bcrypt = require('bcryptjs');

// サンプルユーザーデータ（実際の運用ではデータベースを使用）
const users = [
  {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    passwordHash: '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.gS8v.m', // password123
    firstName: '太郎',
    lastName: '田中',
    phoneNumber: '090-1234-5678',
    enabled: true,
    locked: false,
    mfaEnabled: false
  },
  {
    id: 2,
    username: 'admin',
    email: 'admin@example.com',
    passwordHash: '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.gS8v.m', // password123
    firstName: '管理者',
    lastName: 'システム',
    phoneNumber: '090-9999-9999',
    enabled: true,
    locked: false,
    mfaEnabled: true
  }
];

module.exports = async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ message: 'Method not allowed' });
  }

  try {
    const { username, password } = req.body;

    if (!username || !password) {
      return res.status(400).json({ message: 'ユーザー名とパスワードが必要です' });
    }

    // ユーザーを検索
    const user = users.find(u => u.username === username || u.email === username);
    
    if (!user) {
      return res.status(401).json({ message: 'ユーザー名またはパスワードが正しくありません' });
    }

    if (!user.enabled) {
      return res.status(401).json({ message: 'アカウントが無効化されています' });
    }

    if (user.locked) {
      return res.status(401).json({ message: 'アカウントがロックされています' });
    }

    // パスワード検証
    const isValidPassword = await bcrypt.compare(password, user.passwordHash);
    
    if (!isValidPassword) {
      return res.status(401).json({ message: 'ユーザー名またはパスワードが正しくありません' });
    }

    // ログイン成功
    const { passwordHash, ...userWithoutPassword } = user;
    
    return res.status(200).json({
      message: 'ログインに成功しました',
      user: userWithoutPassword,
      token: `mock-jwt-token-${user.id}-${Date.now()}`
    });

  } catch (error) {
    console.error('Login error:', error);
    return res.status(500).json({ message: '内部サーバーエラーが発生しました' });
  }
}
