import React, { useState, useEffect } from 'react';
import { createClient } from '../../api/clientApi';

export default function ClientFormModal({ isOpen, onClose, onClientCreated }) {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    companyName: '',
    address: '',
    city: '',
    country: '',
    postcode: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen) {
      document.body.classList.add('modal-open');
      setFormData({
        name: '',
        email: '',
        companyName: '',
        address: '',
        city: '',
        country: '',
        postcode: ''
      });
      setError(null);
    } else {
      document.body.classList.remove('modal-open');
    }
    return () => document.body.classList.remove('modal-open');
  }, [isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const newClient = await createClient(formData);
      onClientCreated(newClient);
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to create client');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark"> {/* Using the same dark modal style */}
        <h2 className="text-xl font-semibold mb-4 text-white">Add New Client</h2>
        
        {error && (
          <div className="mb-4 p-3 bg-red-900/20 text-red-400 rounded border border-red-800">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 gap-4">
            {[
              { id: 'name', label: 'Full Name', type: 'text', placeholder: 'John Doe' },
              { id: 'email', label: 'Email', type: 'email', placeholder: 'john@example.com' },
              { id: 'companyName', label: 'Company Name', type: 'text', placeholder: 'Acme Inc.' },
              { id: 'address', label: 'Address', type: 'text', placeholder: '123 Main St' },
              { id: 'city', label: 'City', type: 'text', placeholder: 'New York' },
              { id: 'country', label: 'Country', type: 'text', placeholder: 'United States' },
              { id: 'postcode', label: 'Postal Code', type: 'text', placeholder: '10001' },
            ].map((field) => (
              <div key={field.id}>
                <label htmlFor={field.id} className="block text-sm font-medium text-zinc-300 mb-1">
                  {field.label}
                </label>
                <input
                  id={field.id}
                  name={field.id}
                  type={field.type}
                  placeholder={field.placeholder}
                  value={formData[field.id]}
                  onChange={(e) => setFormData({...formData, [field.id]: e.target.value})}
                  className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                  required
                />
              </div>
            ))}
          </div>
          
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 text-sm rounded border border-zinc-600 text-zinc-300 hover:bg-zinc-800"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 text-sm rounded bg-indigo-600 text-white hover:bg-indigo-700 border border-indigo-700"
            >
              {loading ? (
                <span className="flex items-center justify-center">
                  <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Saving...
                </span>
              ) : 'Add Client'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}