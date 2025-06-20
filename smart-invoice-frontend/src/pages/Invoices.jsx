import React, { useEffect, useState } from 'react';
import InvoiceModal from '../components/invoices/InvoiceModal';
import InvoiceFormModal from '../components/invoices/InvoiceFormModal';
import { useAuth } from '../contexts/AuthContext';

export default function Invoices() {
  const { user } = useAuth();
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [isCreateOpen, setCreateOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [isPaid, setIsPaid] = useState('');
  const [dropdownPosition, setDropdownPosition] = useState(null);

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (search) params.append('search', search);
      if (isPaid !== '') params.append('isPaid', isPaid);
      const res = await fetch(`http://localhost:8080/api/invoices?${params}`, {
        credentials: 'include',
      });
      const data = await res.json();
      setInvoices(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!user) return;
    fetchInvoices();
  }, [search, isPaid]);

  const downloadCsv = () => {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (isPaid !== '') params.append('isPaid', isPaid);
    window.open(`http://localhost:8080/api/export/invoices/csv?${params}`, '_blank');
  };

  const downloadPdf = (id) => {
    window.open(`http://localhost:8080/api/invoices/${id}/pdf`, '_blank');
  };

  if (loading) return <p>Loading invoices...</p>;

  return (
    <div className="p-6">
      <header className="flex items-center justify-between py-4">
        <h1 className="text-3xl font-bold tracking-tight">Invoices</h1>
        <div className="flex gap-2">
          <button
            onClick={downloadCsv}
            className="px-4 py-2 rounded bg-green-600 hover:bg-green-700 text-white"
          >
            Download CSV
          </button>
          <button
            onClick={() => setCreateOpen(true)}
            className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-500 text-white"
          >
            + Create Invoice
          </button>
        </div>
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
          <label className="mb-1 text-sm font-medium text-white">Sort By</label>
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
        <div className="overflow-x-auto shadow-lg rounded-xl border border-zinc-700 bg-[#242424]">
          <table className="w-full">
            <thead>
              <tr className="border-b border-zinc-700">
                {['Invoice No','Client','Date','Total','Paid','Actions'].map((h) => (
                  <th
                    key={h}
                    className={`px-10 py-3 text-center text-sm font-semibold text-zinc-300 ${h === 'Actions' ? 'w-[200px]' : ''}`}
                  >
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {invoices.map((invoice) => (
                <tr
                  key={invoice.id}
                  className="border-b border-zinc-700 hover:bg-zinc-800/40 transition-colors"
                >
                  <td className="px-10 py-4 text-center text-zinc-300">{invoice.invoiceNumber}</td>
                  <td className="px-10 py-4 text-center text-zinc-300 min-w-[150px]">{invoice.clientName}</td>
                  <td className="px-10 py-4 text-center text-zinc-300 min-w-[100px]">{invoice.issueDate}</td>
                  <td className="px-10 py-4 text-center text-zinc-300 min-w-[100px]">£{invoice.totalAmount}</td>
                  <td className="px-10 py-4 text-center text-zinc-300">{invoice.isPaid ? 'Yes' : 'No'}</td>
                  <td className="px-10 py-4 text-center relative">
                    <button
                      onClick={(e) => {
                        const rect = e.currentTarget.getBoundingClientRect();
                        setDropdownPosition({
                          top: rect.bottom + window.scrollY,
                          left: rect.left + window.scrollX,
                          invoice,
                        });
                      }}
                      className="px-3 py-1.5 text-xs rounded-md bg-zinc-700 hover:bg-zinc-600 text-white"
                    >
                      Options
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {dropdownPosition && (
        <div
          className="fixed z-50 bg-zinc-800 border border-zinc-700 rounded shadow-md p-2 space-y-1"
          style={{ top: dropdownPosition.top, left: dropdownPosition.left }}
        >
          <button
            onClick={() => {
              downloadPdf(dropdownPosition.invoice.id);
              setDropdownPosition(null);
            }}
            className="w-full text-left text-sm px-2 py-1 hover:bg-zinc-700 text-white"
          >PDF</button>
          <button
            onClick={() => {
              setSelectedInvoice(dropdownPosition.invoice);
              setDropdownPosition(null);
            }}
            className="w-full text-left text-sm px-2 py-1 hover:bg-zinc-700 text-white"
          >View</button>
          {!dropdownPosition.invoice.isPaid && (
            <button
              onClick={async () => {
                await fetch(`http://localhost:8080/api/invoices/${dropdownPosition.invoice.id}/mark-paid`, {
                  method: 'PATCH',
                  credentials: 'include',
                });
                fetchInvoices();
                setDropdownPosition(null);
              }}
              className="w-full text-left text-sm px-2 py-1 hover:bg-zinc-700 text-white"
            >Mark Paid</button>
          )}
          <button
            onClick={async () => {
              await fetch(`http://localhost:8080/api/invoices/${dropdownPosition.invoice.id}/email`, {
                method: 'POST',
                credentials: 'include',
              });
              alert('Invoice emailed');
              setDropdownPosition(null);
            }}
            className="w-full text-left text-sm px-2 py-1 hover:bg-zinc-700 text-white"
          >Email</button>
          <button
            onClick={async () => {
              if (confirm('Delete this invoice?')) {
                await fetch(`http://localhost:8080/api/invoices/${dropdownPosition.invoice.id}`, {
                  method: 'DELETE',
                  credentials: 'include',
                });
                fetchInvoices();
              }
              setDropdownPosition(null);
            }}
            className="w-full text-left text-sm px-2 py-1 text-rose-400 hover:bg-zinc-700"
          >Delete</button>
          <button
            onClick={() => setDropdownPosition(null)}
            className="w-full text-left text-sm px-2 py-1 text-zinc-400 hover:bg-zinc-700"
          >Close</button>
        </div>
      )}

      <InvoiceFormModal
        isOpen={isCreateOpen}
        onClose={() => setCreateOpen(false)}
        onCreated={() => {
          setCreateOpen(false);
          fetchInvoices();
        }}
      />

      <InvoiceModal
        isOpen={!!selectedInvoice}
        invoice={selectedInvoice}
        onClose={() => setSelectedInvoice(null)}
      />
    </div>
  );
}
