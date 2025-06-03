import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../api/api';

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <nav className="bg-zinc-800 px-6 py-3 border-b border-zinc-700">
      <div className="flex items-center justify-between">
        <div className="flex gap-6"> {/* Changed from space-x-6 to gap-6 */}
          <Link 
            to="/clients" 
            className="text-white hover:text-indigo-300 px-3 py-2 rounded-md hover:bg-zinc-700/50 transition-colors text-sm font-medium"
          >
            Clients
          </Link>
          <Link 
            to="/products" 
            className="text-white hover:text-indigo-300 px-3 py-2 rounded-md hover:bg-zinc-700/50 transition-colors text-sm font-medium"
          >
            Products
          </Link>
          <Link 
            to="/invoices" 
            className="text-white hover:text-indigo-300 px-3 py-2 rounded-md hover:bg-zinc-700/50 transition-colors text-sm font-medium"
          >
            Invoices
          </Link>
        </div>
        <button
          onClick={handleLogout}
          className="text-white hover:text-rose-300 px-3 py-2 rounded-md hover:bg-zinc-700/50 transition-colors text-sm font-medium"
        >
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;