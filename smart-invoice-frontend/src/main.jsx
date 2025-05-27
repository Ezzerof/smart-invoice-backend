import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom';

import Login from './pages/Login';
import Clients from './pages/Clients';
import Products from './pages/Products';
import Invoices from './pages/Invoices';

import './index.css';

function Navbar() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await fetch('/logout', { method: 'POST' });
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow p-4 flex justify-between items-center fixed top-0 left-0 right-0 z-50">
      <div className="space-x-4">
        <Link to="/clients" className="text-gray-700 hover:text-gray-900">Clients</Link>
        <Link to="/products" className="text-gray-700 hover:text-gray-900">Products</Link>
        <Link to="/invoices" className="text-gray-700 hover:text-gray-900">Invoices</Link>
      </div>
      <button
        onClick={handleLogout}
        className="text-red-500 hover:text-red-700 font-semibold"
      >
        Logout
      </button>
    </nav>
  );
}

function App() {
  const isLoggedIn = true; // TODO: replace with real auth check

  if (!isLoggedIn) return <Navigate to="/login" />;

   return (
      <>
        {/* Show navbar only if logged in and not on login page */}
        {isLoggedIn && location.pathname !== '/login' && <Navbar />}
        <div className={isLoggedIn && location.pathname !== '/login' ? 'pt-16' : ''}>
          <Routes>
            <Route path="/clients" element={<Clients />} />
            <Route path="/products" element={<Products />} />
            <Route path="/invoices" element={<Invoices />} />
            <Route path="/login" element={<Login />} />
            <Route path="*" element={<Navigate to="/clients" />} />
          </Routes>
        </div>
      </>
    );
  }

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
