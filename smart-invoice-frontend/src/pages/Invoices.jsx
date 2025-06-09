import React, { useEffect, useState } from 'react';
import InvoiceModal from '../components/invoices/InvoiceModal';

export default function Invoices() {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [search, setSearch] = useState('');
  const [isPaid, setIsPaid] = useState('');

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (search) params.append("search", search);
      if (isPaid !== '') params.append("isPaid", isPaid);

      const res = await fetch(`http://localhost:8080/api/invoices?${params.toString()}`, {
        credentials: 'include',
      });
      const data = await res.json();
      setInvoices(data);
    } catch (err) {
      console.error("Failed to load invoices", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, [search, isPaid]);

  const openInvoiceModal = (id) => {
    fetch(`http://localhost:8080/api/invoices/${id}`, {
      credentials: 'include',
    })
      .then(res => res.json())
      .then(setSelectedInvoice)
      .catch(err => console.error("Failed to fetch invoice details", err));
  };

  const deleteInvoice = async (id) => {
    await fetch(`http://localhost:8080/api/invoices/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });
  };

  const markAsPaid = async (id) => {
    await fetch(`http://localhost:8080/api/invoices/${id}/mark-paid`, {
      method: 'PATCH',
      credentials: 'include',
    });

    const res = await fetch('http://localhost:8080/api/invoices', {
      credentials: 'include',
    });
    const updated = await res.json();
    setInvoices(updated);
  };

  const sendInvoiceEmail = async (id) => {
    await fetch(`http://localhost:8080/api/invoices/${id}/email`, {
      method: 'POST',
      credentials: 'include',
    });
  };

  if (loading) return <p>Loading invoices...</p>;

  return (
    <div className="p-6">
      <header className="flex items-center justify-between py-4">
        <h1 className="text-3xl font-bold tracking-tight">Invoices</h1>
      </header>

      <div className="mb-6 flex flex-col md:flex-row md:items-end gap-4">
        <div className="flex flex-col w-full md:w-1/2">
          <label className="mb-1 text-sm font-medium text-white">Search</label>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Search by invoice number..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="flex-1 px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
            />
            <button
              onClick={fetchInvoices}
              className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-700 text-white"
            >
              Search
            </button>
          </div>
        </div>

        <div className="flex flex-col">
          <label className="mb-1 text-sm font-medium text-white">Payment Status</label>
          <select
            value={isPaid}
            onChange={(e) => setIsPaid(e.target.value)}
            className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
          >
            <option value="">All</option>
            <option value="true">Paid</option>
            <option value="false">Unpaid</option>
          </select>
        </div>
      </div>


      {invoices.length === 0 ? (
        <p>No invoices found.</p>
      ) : (
        <table className="w-full table-auto border border-zinc-400">
          <thead>
            <tr className="bg-zinc-800">
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Invoice No</th>
              <th className="px-4 py-2 text-left">Client</th>
              <th className="px-4 py-2 text-left">Date</th>
              <th className="px-4 py-2 text-left">Total</th>
              <th className="px-4 py-2 text-left">Paid</th>
              <th className="px-4 py-2 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {invoices.map((invoice) => (
              <tr key={invoice.id} className="border-t border-zinc-400">
                <td className="p-2">{invoice.id}</td>
                <td className="p-2">{invoice.invoiceNumber}</td>
                <td className="p-2">{invoice.clientName}</td>
                <td className="p-2">{invoice.issueDate}</td>
                <td className="p-2">£{invoice.totalAmount}</td>
                <td className="p-2">{invoice.isPaid ? "Yes" : "No"}</td>
                <td className="p-2 flex flex-wrap gap-2 text-sm">
                  <button
                    className="px-3 py-1 rounded bg-blue-600 hover:bg-blue-700 text-white"
                    onClick={() => openInvoiceModal(invoice.id)}
                  >
                    View
                  </button>
                  {!invoice.isPaid && (
                    <button
                      className="px-3 py-1 rounded bg-green-600 hover:bg-green-700 text-white"
                      onClick={async () => {
                        await markAsPaid(invoice.id);
                        await fetchInvoices();
                      }}
                    >
                      Mark as Paid
                    </button>
                  )}
                  <button
                    className="px-3 py-1 rounded bg-yellow-600 hover:bg-yellow-700 text-white"
                    onClick={async () => {
                      await sendInvoiceEmail(invoice.id);
                      alert("Invoice sent!");
                    }}
                  >
                    Email
                  </button>
                  <button
                    className="px-3 py-1 rounded bg-red-600 hover:bg-red-700 text-white"
                    onClick={async () => {
                      if (confirm("Are you sure you want to delete this invoice?")) {
                        await deleteInvoice(invoice.id);
                        setInvoices(prev => prev.filter(i => i.id !== invoice.id));
                      }
                    }}
                  >
                    Delete
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
