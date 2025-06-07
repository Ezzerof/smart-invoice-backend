import React, { useEffect } from 'react';

export default function InvoiceModal({ isOpen, invoice, onClose }) {
  useEffect(() => {
    if (isOpen) {
      document.body.classList.add('modal-open');
    } else {
      document.body.classList.remove('modal-open');
    }

    return () => document.body.classList.remove('modal-open');
  }, [isOpen]);

  if (!isOpen || !invoice) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content-dark">
        <h2 className="text-xl font-semibold mb-4 text-white">
          Invoice #{invoice.id}
        </h2>

        <div className="space-y-2 text-sm text-zinc-300">
          <p><strong>Client:</strong> {invoice.clientName}</p>
          <p><strong>Date:</strong> {invoice.issueDate}</p>
          <p><strong>Total:</strong> £{invoice.totalAmount}</p>
          <p><strong>Paid:</strong> {invoice.paid ? 'Yes' : 'No'}</p>
          {invoice.description && (
            <p><strong>Description:</strong> {invoice.description}</p>
          )}
        </div>

        <div className="flex justify-end gap-3 pt-6">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm rounded border border-zinc-600 text-zinc-300 hover:bg-zinc-800"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
