import React, { useState, useEffect } from 'react';
import {
  fetchClientsWithFilters,
  deleteClient,
} from '../api/clientApi';
import ClientTable from '../components/clients/ClientTable';
import ClientFormModal from '../components/clients/ClientFormModal';
import ClientEditModal from '../components/clients/ClientEditModal';
import { useAuth } from '../contexts/AuthContext';

export default function Clients() {
  const { user } = useAuth();
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isCreateOpen, setCreateOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);

  const [keyword, setKeyword] = useState('');
  const [sortBy, setSortBy] = useState('');

  const loadClients = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (keyword) params.append('keyword', keyword);
      if (sortBy) params.append('sortBy', sortBy);

      const data = await fetchClientsWithFilters(params.toString());
      setClients(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!user) return;
    loadClients();
  }, [user, sortBy]);




  const handleDelete = async (client) => {
    if (!window.confirm(`Delete ${client.name}?`)) return;
    await deleteClient(client.id);
    setClients((cur) => cur.filter((c) => c.id !== client.id));
  };

  const handleUpdate = (updated) =>
    setClients((cur) =>
      cur.map((c) => (c.id === updated.id ? updated : c))
    );

  if (loading) return <p className="p-6">Loading clients…</p>;
  if (error) return <p className="p-6 text-rose-400">Error: {error}</p>;

  return (
    <section className="space-y-8">
      <header className="flex items-center justify-between py-4">
        <h1 className="text-3xl font-bold tracking-tight">Clients</h1>
        {user && (
          <button
            onClick={() => setCreateOpen(true)}
            className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-500 text-white"
          >
            + Add Client
          </button>
        )}
      </header>

      {/* Search & Sort */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="flex flex-col">
          <label className="mb-1 text-sm font-medium text-white">Search</label>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Search by name, email or company..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="flex-1 px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
            />
            <button
              onClick={loadClients}
              className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-700 text-white"
            >
              Search
            </button>
          </div>
        </div>

        <div className="flex flex-col">
          <label className="mb-1 text-sm font-medium text-white">Sort By</label>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
          >
            <option value="">Sort By</option>
            <option value="name">Name (A–Z)</option>
            <option value="-name">Name (Z–A)</option>
            <option value="city">City</option>
            <option value="country">Country</option>
          </select>
        </div>
      </div>

      <ClientTable
        clients={clients}
        onEdit={(c) => setEditTarget(c)}
        onDelete={handleDelete}
      />

      <ClientFormModal
        isOpen={isCreateOpen}
        onClose={() => setCreateOpen(false)}
        onClientCreated={(c) => setClients((list) => [...list, c])}
      />
      <ClientEditModal
        isOpen={!!editTarget}
        client={editTarget}
        onClose={() => setEditTarget(null)}
        onUpdated={handleUpdate}
      />
    </section>
  );
}
