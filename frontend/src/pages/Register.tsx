import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * 登録コンポーネント
 * 認証処理を無効化：常にDashboardにリダイレクト
 */
const Register: React.FC = () => {
  const navigate = useNavigate();
  
  // 認証処理を無効化：即座にDashboardにリダイレクト
  useEffect(() => {
    navigate('/dashboard', { replace: true });
  }, [navigate]);

  // 何も表示しない（リダイレクト中）
  return null;
};

export default Register;
