import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="w-full bg-gray-900 text-white py-3 px-6 flex justify-between items-center">
      <div className="space-x-4">
        <Link to="/clients" className="hover:underline">Clients</Link>
        <Link to="/products" className="hover:underline">Products</Link>
        <Link to="/invoices" className="hover:underline">Invoices</Link>
      </div>
      <button
        onClick={handleLogout}
        className="bg-red-600 hover:bg-red-700 px-4 py-2 rounded font-semibold"
      >
        Logout
      </button>
    </nav>
  );
}
