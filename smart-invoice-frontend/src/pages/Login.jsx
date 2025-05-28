import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const auth = useAuth();
  const navigate = useNavigate();

  const validate = () => {
    const errs = {};
    if (!username.trim()) errs.username = 'Username is required';
    if (!password.trim()) errs.password = 'Password is required';
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setLoading(true);
    try {
      const formData = new URLSearchParams();
      formData.append('username', username);
      formData.append('password', password);

      const response = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString(),
        credentials: 'include',
      });

      if (!response.ok) {
        setErrors({ form: 'Invalid username or password' });
        setLoading(false);
        return;
      }

      auth.login(username);
      navigate('/clients'); // Redirect to protected page after login

    } catch {
      setErrors({ form: 'Network error' });
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="min-h-screen flex flex-col justify-center items-center px-4">
      {/* Placeholder for logo */}
      <div className="mb-8">
        <img src="/logo.png" alt="Logo" className="h-24 mx-auto" />
      </div>
      <form onSubmit={handleSubmit} className="w-full max-w-sm bg-white p-8 rounded shadow">
              {errors.form && <p className="text-red-500 text-center mb-4">{errors.form}</p>}
              <label htmlFor="username" className="block font-semibold mb-1">Username</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                disabled={loading}
                className={`w-full border rounded px-3 py-2 mb-4 ${
                  errors.username ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {errors.username && <p className="text-red-500 mb-4">{errors.username}</p>}

              <label htmlFor="password" className="block font-semibold mb-1">Password</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                disabled={loading}
                className={`w-full border rounded px-3 py-2 mb-6 ${
                  errors.password ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {errors.password && <p className="text-red-500 mb-6">{errors.password}</p>}

              <button
                type="submit"
                disabled={loading}
                className={`w-full bg-blue-600 text-white py-2 rounded font-semibold hover:bg-blue-700 transition-colors ${
                  loading ? 'opacity-50 cursor-not-allowed' : ''
                }`}
              >
                {loading ? 'Logging in...' : 'Login'}
              </button>
            </form>
          </div>
        );
      }
