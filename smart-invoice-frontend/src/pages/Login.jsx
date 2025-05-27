import React, { useState } from 'react';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

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
      // Simulate login request
      await new Promise(r => setTimeout(r, 1500));
      alert(`Logged in as ${username}`);
      // TODO: Redirect or update app state
    } catch (err) {
      alert('Login failed');
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
        <div className="mb-4">
          <label htmlFor="username" className="block text-left font-semibold mb-1">Username</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring ${
              errors.username ? 'border-red-500' : 'border-gray-300'
            }`}
            disabled={loading}
          />
          {errors.username && <p className="text-red-500 text-sm mt-1">{errors.username}</p>}
        </div>
        <div className="mb-6">
          <label htmlFor="password" className="block text-left font-semibold mb-1">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring ${
              errors.password ? 'border-red-500' : 'border-gray-300'
            }`}
            disabled={loading}
          />
          {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password}</p>}
        </div>
        <button
          type="submit"
          className={`w-full bg-blue-600 text-white py-2 rounded font-semibold hover:bg-blue-700 transition-colors ${
            loading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
          disabled={loading}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
}
