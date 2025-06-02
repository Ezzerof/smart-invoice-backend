import React from 'react';

export default function ClientTable({ clients }) {
  return (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white">
        <thead>
          <tr className="bg-gray-100">
            <th className="py-3 px-4 text-left">Name</th>
            <th className="py-3 px-4 text-left">Company</th>
            <th className="py-3 px-4 text-left">Email</th>
            <th className="py-3 px-4 text-left">Location</th>
            <th className="py-3 px-4 text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          {clients.map((client) => (
            <tr key={client.id} className="border-b">
              <td className="py-3 px-4">{client.name}</td>
              <td className="py-3 px-4">{client.companyName}</td>
              <td className="py-3 px-4">{client.email}</td>
              <td className="py-3 px-4">{client.city}, {client.country}</td>
              <td className="py-3 px-4">
                <button className="text-blue-600 hover:text-blue-800 mr-3">
                  Edit
                </button>
                <button className="text-red-600 hover:text-red-800">
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}