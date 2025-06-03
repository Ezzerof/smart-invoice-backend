import React from 'react';

export default function ClientTable({ clients, onEdit, onDelete }) {
  return (
    <div className="overflow-x-auto shadow-lg rounded-xl border border-zinc-700 bg-[#242424]">
      <table className="w-full">
        <thead>
          <tr className="border-b border-zinc-700">
            {["Name", "Company", "Email", "Location", "Actions"].map((h) => (
              <th 
                key={h} 
                className={`px-6 py-3 text-center text-sm font-semibold text-zinc-300 ${
                  h === "Actions" ? "w-[100px]" : ""
                }`}
              >
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {clients.map((c) => (
            <tr
              key={c.id}
              className="border-b border-zinc-700 hover:bg-zinc-800/40 transition-colors"
            >
              <td className="px-6 py-4 text-center">
                <div className="text-white font-medium">{c.name}</div>
              </td>
              <td className="px-6 py-4 text-center min-w-[180px]">
                <div className="text-zinc-300">{c.companyName}</div>
              </td>
              <td className="px-6 py-4 text-center min-w-[200px]">
                <div className="text-zinc-300">{c.email}</div>
              </td>
              <td className="px-6 py-4 text-center min-w-[150px]">
                <div className="text-zinc-300">
                  {c.city}, {c.country}
                </div>
              </td>
              <td className="px-6 py-4">
                <div className="flex justify-end gap-4 pr-4">
                  <button
                    onClick={() => onEdit(c)}
                    className="px-4 py-1.5 text-xs rounded-md bg-indigo-600 hover:bg-indigo-500 text-white transition-colors"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(c)}
                    className="px-4 py-1.5 text-xs rounded-md bg-rose-600 hover:bg-rose-500 text-white transition-colors"
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}