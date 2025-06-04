import React from 'react';
import { useNavigate } from 'react-router-dom';
import { logout } from '../api/api';
import { NavLink } from 'react-router-dom';

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
      <div className="flex justify-between items-center">
        <div className="navbar flex">
          <NavLink 
            to="/clients" 
            className={({ isActive }) => 
              `px-3 py-2 rounded-md text-sm font-medium transition-colors
              ${isActive ? 'bg-zinc-700/50 text-indigo-300' : 'text-white hover:text-indigo-300 hover:bg-zinc-700/50'}`
            }
          >
            Clients
          </NavLink>
          <NavLink 
            to="/products" 
            className={({ isActive }) => 
              `px-3 py-2 rounded-md text-sm font-medium transition-colors
              ${isActive ? 'bg-zinc-700/50 text-indigo-300' : 'text-white hover:text-indigo-300 hover:bg-zinc-700/50'}`
            }
          >
            Products
          </NavLink>
          <NavLink 
            to="/invoices" 
            className={({ isActive }) => 
              `px-3 py-2 rounded-md text-sm font-medium transition-colors
              ${isActive ? 'bg-zinc-700/50 text-indigo-300' : 'text-white hover:text-indigo-300 hover:bg-zinc-700/50'}`
            }
          >
            Invoices
          </NavLink>
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