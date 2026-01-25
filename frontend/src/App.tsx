import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import Navbar from './components/Navbar';
import ErrorBoundary from './components/ErrorBoundary';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Accounts from './pages/Accounts';
import Transactions from './pages/Transactions';
import Transfer from './pages/Transfer';
import Services from './pages/Services';
import Investments from './pages/Investments';
import './App.css';

const queryClient = new QueryClient();

// メインコンテンツのラッパーコンポーネント
const MainContent: React.FC = () => {
  const location = useLocation();
  const isServicesPage = location.pathname === '/services';
  
  if (isServicesPage) {
    return (
      <main>
        <Services />
      </main>
    );
  }
  
  return (
    <main className="container mx-auto px-4 py-8">
      <Routes>
        <Route path="/" element={<Dashboard />} />
        {/* 認証画面を非表示：ログイン・登録画面をDashboardにリダイレクト */}
        <Route path="/login" element={<Dashboard />} />
        <Route path="/register" element={<Dashboard />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/accounts" element={<Accounts />} />
        <Route path="/transactions" element={<Transactions />} />
        <Route path="/transfer" element={<Transfer />} />
        <Route path="/investments" element={<Investments />} />
      </Routes>
    </main>
  );
};

function App() {
  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Navbar />
            <MainContent />
          </div>
        </Router>
      </QueryClientProvider>
    </ErrorBoundary>
  );
}

export default App;
