import React, { useEffect, useState } from 'react';
import ProductPickerModal from './ProductPickerModal';

export default function InvoiceFormModal({ isOpen, onClose, onCreated }) {
  const [clients, setClients] = useState([]);
  const [invoiceItems, setInvoiceItems] = useState([]);
  const [selectedClientId, setSelectedClientId] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [showProductPicker, setShowProductPicker] = useState(false);

  useEffect(() => {
    if (!isOpen) return;
    fetch('/api/clients', { credentials: 'include' })
      .then(res => res.json())
      .then(setClients);
  }, [isOpen]);

  const handleProductSelect = (selectedProducts) => {
    setInvoiceItems(items => {
      const existingIds = new Set(items.map(i => i.id));
      const newItems = selectedProducts.filter(p => !existingIds.has(p.id)).map(p => ({ ...p, quantity: 1 }));
      return [...items, ...newItems];
    });
  };

  const removeProduct = (id) => {
    setInvoiceItems(items => items.filter(p => p.id !== id));
  };

  const updateItem = (id, field, value) => {
    setInvoiceItems(items =>
      items.map(item =>
        item.id === id ? { ...item, [field]: value } : item
      )
    );
  };

  const total = invoiceItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleSubmit = async () => {
    const payload = {
      clientId: selectedClientId,
      dueDate,
      products: invoiceItems.map(({ id, quantity, price }) => ({
        productId: id,
        quantity,
        price
      }))
    };

    const res = await fetch('/api/invoices', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      const created = await res.json();
      onCreated(created);
      onClose();
    } else {
      alert('Failed to create invoice');
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark">
        <h2 className="text-xl font-semibold mb-4 text-white">Create Invoice</h2>

        <div className="space-y-4">
          <div>
            <label className="text-sm font-medium text-white">Client</label>
            <select
              value={selectedClientId}
              onChange={(e) => setSelectedClientId(e.target.value)}
              className="w-full px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
            >
              <option value="">Select a client</option>
              {clients.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-sm font-medium text-white">Products</label>
            <button
              onClick={() => setShowProductPicker(true)}
              className="mt-2 px-3 py-2 rounded bg-indigo-600 hover:bg-indigo-500 text-white border border-indigo-400 shadow-md"
            >
              Select Products
            </button>
          </div>

          <div className="space-y-2 max-h-48 overflow-y-auto">
            {invoiceItems.map((item) => (
              <div key={item.id} className="flex flex-wrap items-center gap-2 border border-zinc-700 rounded px-2 py-1">
                <span className="text-white w-full sm:w-auto flex-1 min-w-[150px]">{item.name}</span>
                <input
                  type="number"
                  min={1}
                  value={item.quantity}
                  onChange={(e) => updateItem(item.id, 'quantity', parseInt(e.target.value))}
                  className="w-20 px-2 py-1 rounded border border-zinc-600 bg-zinc-800 text-white"
                />
                <input
                  type="number"
                  min={0}
                  value={item.price}
                  onChange={(e) => updateItem(item.id, 'price', parseFloat(e.target.value))}
                  className="w-24 px-2 py-1 rounded border border-zinc-600 bg-zinc-800 text-white"
                />
                <button
                  onClick={() => removeProduct(item.id)}
                  className="px-3 py-1 text-xs rounded bg-rose-600 hover:bg-rose-500 text-white"
                >
                  Remove
                </button>
              </div>
            ))}
          </div>

          <div>
            <label className="text-sm font-medium text-white">Due Date</label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
            />
          </div>

          <div className="text-white text-lg font-semibold">Total: £{total.toFixed(2)}</div>

          <div className="flex justify-end gap-3 pt-6">
            <button
              onClick={onClose}
              className="px-4 py-2 rounded border border-zinc-600 text-zinc-300 hover:bg-zinc-800"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              className="px-4 py-2 rounded bg-green-600 hover:bg-green-500 text-white"
              disabled={!selectedClientId || invoiceItems.length === 0 || !dueDate}
            >
              Create Invoice
            </button>
          </div>
        </div>
      </div>

      <ProductPickerModal
        isOpen={showProductPicker}
        onClose={() => setShowProductPicker(false)}
        onSelect={handleProductSelect}
        preselectedIds={invoiceItems.map(item => item.id)}
      />
    </div>
  );
}
