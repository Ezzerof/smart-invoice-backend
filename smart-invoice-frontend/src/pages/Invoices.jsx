import React, { useEffect, useState } from 'react';
import InvoiceModal from '../components/invoices/InvoiceModal';

export default function Invoices() {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedInvoice, setSelectedInvoice] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/invoices', {
      credentials: 'include',
    })
      .then((res) => res.json())
      .then(setInvoices)
      .catch((err) => console.error("Failed to load invoices", err))
      .finally(() => setLoading(false));
  }, []);

  const openInvoiceModal = (id) => {
    fetch(`http://localhost:8080/api/invoices/${id}`, {
      credentials: 'include',
    })
      .then(res => res.json())
      .then(setSelectedInvoice)
      .catch(err => console.error("Failed to fetch invoice details", err));
  };

  if (loading) return <p>Loading invoices...</p>;

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Invoices</h1>
      {invoices.length === 0 ? (
        <p>No invoices found.</p>
      ) : (
        <table className="w-full table-auto border border-zinc-700">
          <thead>
            <tr className="bg-zinc-800">
              <th className="p-2 text-left">ID</th>
              <th className="p-2 text-left">Client</th>
              <th className="p-2 text-left">Date</th>
              <th className="p-2 text-left">Total</th>
              <th className="p-2 text-left">Paid</th>
              <th className="p-2 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {invoices.map((invoice) => (
              <tr key={invoice.id} className="border-t border-zinc-700">
                <td className="p-2">{invoice.id}</td>
                <td className="p-2">{invoice.clientName}</td>
                <td className="p-2">{invoice.issueDate}</td>
                <td className="p-2">£{invoice.totalAmount}</td>
                <td className="p-2">{invoice.paid ? "Yes" : "No"}</td>
                <td className="p-2">
                  <button
                    className="text-blue-400 hover:underline mr-2"
                    onClick={() => openInvoiceModal(invoice.id)}
                  >
                    View
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <InvoiceModal
        isOpen={!!selectedInvoice}
        invoice={selectedInvoice}
        onClose={() => setSelectedInvoice(null)}
      />
    </div>
  );
}
