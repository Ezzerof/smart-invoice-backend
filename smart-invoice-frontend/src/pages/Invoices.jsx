import React, { useEffect, useState } from 'react';

export default function Invoices() {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8080/api/invoices', {
      credentials: 'include',
    })
      .then((res) => res.json())
      .then((data) => setInvoices(data))
      .catch((err) => console.error("Failed to load invoices", err))
      .finally(() => setLoading(false));
  }, []);

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
                  {/* Placeholder for future buttons */}
                  <button className="text-blue-400 hover:underline mr-2">View</button>
                  <button className="text-red-400 hover:underline">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
