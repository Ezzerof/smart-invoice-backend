import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../api/api';

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
    window.location.reload();
  };

  return (
    <nav>
      <Link to="/clients">Clients</Link>
      <Link to="/products">Products</Link>
      <Link to="/invoices">Invoices</Link>
      <button onClick={handleLogout}>Logout</button>
    </nav>
  );
};

export default Navbar;
