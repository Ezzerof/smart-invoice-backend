// components/invoices/ProductPickerModal.jsx
import React, { useEffect, useState } from 'react';

export default function ProductPickerModal({ isOpen, onClose, onSelect, preselectedIds = [] }) {
  const [products, setProducts] = useState([]);
  const [selected, setSelected] = useState(new Set(preselectedIds));

  useEffect(() => {
    if (isOpen) {
      fetch('/api/products', { credentials: 'include' })
        .then(res => res.json())
        .then(data => setProducts(data));
    }
  }, [isOpen]);

  const toggle = (id) => {
    setSelected(prev => {
      const copy = new Set(prev);
      copy.has(id) ? copy.delete(id) : copy.add(id);
      return copy;
    });
  };

  const handleConfirm = () => {
    const selectedProducts = products.filter(p => selected.has(p.id));
    onSelect(selectedProducts);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark max-h-[90vh] overflow-y-auto">
        <h2 className="text-lg font-bold text-white mb-4">Select Products</h2>
        <ul className="space-y-2 max-h-[60vh] overflow-y-auto">
          {products.map(p => (
            <li key={p.id} className="flex items-center justify-between p-2 border border-zinc-700 rounded">
              <label className="flex items-center gap-3 text-white">
                <input
                  type="checkbox"
                  checked={selected.has(p.id)}
                  onChange={() => toggle(p.id)}
                />
                <span>{p.name} (£{p.price.toFixed(2)})</span>
              </label>
            </li>
          ))}
        </ul>
        <div className="flex justify-end gap-3 pt-6">
          <button onClick={handleConfirm} className="px-4 py-2 text-sm bg-indigo-600 text-white rounded">Add</button>
          <button onClick={onClose} className="px-4 py-2 text-sm border border-zinc-600 text-zinc-300 rounded">Cancel</button>
        </div>
      </div>
    </div>
  );
}
