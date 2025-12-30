const bcrypt = require('bcryptjs');

// サンプルユーザーデータ（実際の運用ではデータベースを使用）
let users = [
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
    const { username, email, firstName, lastName, phoneNumber, password, confirmPassword } = req.body;

    // 入力値の検証
    if (!username || !email || !firstName || !lastName || !phoneNumber || !password || !confirmPassword) {
      return res.status(400).json({ message: 'すべての項目を入力してください' });
    }

    if (password !== confirmPassword) {
      return res.status(400).json({ message: 'パスワードが一致しません' });
    }

    if (password.length < 8) {
      return res.status(400).json({ message: 'パスワードは8文字以上で入力してください' });
    }

    // ユーザー名とメールアドレスの重複チェック
    const existingUser = users.find(u => u.username === username || u.email === email);
    if (existingUser) {
      if (existingUser.username === username) {
        return res.status(400).json({ message: 'このユーザー名は既に使用されています' });
      }
      if (existingUser.email === email) {
        return res.status(400).json({ message: 'このメールアドレスは既に使用されています' });
      }
    }

    // パスワードのハッシュ化
    const saltRounds = 12;
    const passwordHash = await bcrypt.hash(password, saltRounds);

    // 新しいユーザーの作成
    const newUser = {
      id: users.length + 1,
      username,
      email,
      passwordHash,
      firstName,
      lastName,
      phoneNumber,
      enabled: true,
      locked: false,
      mfaEnabled: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    // ユーザーリストに追加
    users.push(newUser);

    // パスワードを除いたユーザー情報を返す
    const { passwordHash: _, ...userWithoutPassword } = newUser;

    return res.status(201).json({
      message: 'アカウントの登録が完了しました',
      user: userWithoutPassword
    });

  } catch (error) {
    console.error('Registration error:', error);
    return res.status(500).json({ message: '内部サーバーエラーが発生しました' });
  }
}
