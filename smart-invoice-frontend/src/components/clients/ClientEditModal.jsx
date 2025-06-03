import React, { useState, useEffect } from "react";
import { updateClient } from "../../api/clientApi";

export default function ClientEditModal({ isOpen, onClose, client, onUpdated }) {
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
      if (client) setFormData(client);
    } else {
      document.body.classList.remove('modal-open');
    }
    return () => document.body.classList.remove('modal-open');
  }, [isOpen, client]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const updated = await updateClient(client.id, formData);
      onUpdated(updated);
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to update client');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark"> {/* Changed to modal-content-dark */}
        <h2 className="text-xl font-semibold mb-4 text-white">Edit Client</h2>
        {error && (
          <p className="mb-4 text-rose-400 bg-rose-600/10 p-2 rounded">{error}</p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 gap-4">
            {["name", "email", "companyName", "address", "city", "country", "postcode"].map(
              (field) => (
                <div key={field} className="space-y-1">
                  <label className="block text-sm text-zinc-300 capitalize">{field}</label>
                  <input
                    name={field}
                    value={formData[field] || ""}
                    onChange={handleChange}
                    className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                    required
                  />
                </div>
              )
            )}
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
              {loading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}