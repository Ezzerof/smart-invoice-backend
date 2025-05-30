import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import api from './api/api';

const App = () => {
  const [user, setUser] = useState(null);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    api.get('/auth/me')
      .then(res => setUser(res.data.username))
      .catch(() => setUser(null))
      .finally(() => setChecking(false));
  }, []);

  if (checking) return <p>Loading...</p>;

  return (
    <Routes>
      <Route path="/login" element={<Login setUser={setUser} />} />
      <Route
        path="/"
        element={
          user ? <Dashboard user={user} /> : <Navigate to="/login" />
        }
      />
    </Routes>
  );
};

export default App;
