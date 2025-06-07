import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';

const AppLayout = () => {
  return (
    <div className="min-h-screen bg-zinc-900 text-zinc-100">
      <Navbar />
      <main className="container mx-auto p-4 md:p-6">
        <Outlet />
      </main>
    </div>
  );
};

export default AppLayout;
