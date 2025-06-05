import React, { useState, useEffect } from "react";
import { createProduct } from "../../api/productApi";

export default function ProductFormModal({ isOpen, onClose, onProductCreated }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '0.00',
    currency: 'GBP',
    quantity: 1
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen) document.body.classList.add("modal-open");
    else document.body.classList.remove("modal-open");
    return () => document.body.classList.remove("modal-open");
  }, [isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isNaN(parseFloat(formData.price)) || parseFloat(formData.price) < 0) {
      setError("Price must be a valid non-negative number");
      return;
    }
    if (isNaN(parseInt(formData.quantity)) || parseInt(formData.quantity) < 0) {
      setError("Quantity must be a valid non-negative integer");
      return;
    }

    setLoading(true);
    try {
      const newProduct = await createProduct(formData);
      onProductCreated(newProduct);
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to create product');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark">
        <h2 className="text-xl font-semibold mb-4 text-white">Add Product</h2>
        {error && (
          <p className="mb-4 text-rose-400 bg-rose-600/10 p-2 rounded">{error}</p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 gap-4">
            {['name', 'description'].map((field) => (
              <div key={field} className="space-y-1">
                <label className="block text-sm text-zinc-300 capitalize">{field}</label>
                <input
                  name={field}
                  value={formData[field] || ''}
                  onChange={handleChange}
                  className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                  required
                />
              </div>
            ))}

            <div className="space-y-1">
              <label className="block text-sm text-zinc-300">Price</label>
              <input
                name="price"
                type="number"
                step="0.01"
                min="0"
                value={formData.price}
                onChange={handleChange}
                className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                required
              />
            </div>

            <div className="space-y-1">
              <label className="block text-sm text-zinc-300">Currency</label>
              <select
                name="currency"
                value={formData.currency}
                onChange={handleChange}
                className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                required
              >
                <option value="GBP">GBP (£)</option>
                <option value="USD">USD ($)</option>
                <option value="EUR">EUR (€)</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="block text-sm text-zinc-300">Quantity</label>
              <input
                name="quantity"
                type="number"
                min="0"
                value={formData.quantity}
                onChange={handleChange}
                className="w-full bg-zinc-800 p-2 rounded border border-zinc-700 focus:outline-none focus:ring-1 focus:ring-indigo-500 text-white"
                required
              />
            </div>
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
              {loading ? 'Saving...' : 'Add Product'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}