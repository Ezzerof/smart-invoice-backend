import React, { useState, useEffect } from "react";
import {
  fetchClientsWithFilters,
  deleteClient,
} from "../api/clientApi";
import ClientTable from "../components/clients/ClientTable";
import ClientFormModal from "../components/clients/ClientFormModal";
import ClientEditModal from "../components/clients/ClientEditModal";
import { useAuth } from "../contexts/AuthContext";

export default function Clients() {
  const { user } = useAuth();
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isCreateOpen, setCreateOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);

  const [keyword, setKeyword] = useState("");
  const [city, setCity] = useState("");
  const [country, setCountry] = useState("");
  const [sortBy, setSortBy] = useState("");

  const loadClients = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (keyword) params.append("keyword", keyword);
      if (city) params.append("city", city);
      if (country) params.append("country", country);
      if (sortBy) params.append("sortBy", sortBy);

      const response = await fetchClientsWithFilters(params.toString());
      setClients(response);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadClients();
  }, [keyword, city, country, sortBy]);

  const handleDelete = async (client) => {
    if (!window.confirm(`Delete ${client.name}?`)) return;
    try {
      await deleteClient(client.id);
      setClients((cur) => cur.filter((c) => c.id !== client.id));
    } catch (e) {
      alert(e.message);
    }
  };

  const handleUpdateInList = (updated) =>
    setClients((cur) => cur.map((c) => (c.id === updated.id ? updated : c)));

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

      {/* Filters */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <input
          type="text"
          placeholder="Search keyword..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
        />
        <select
          value={city}
          onChange={(e) => setCity(e.target.value)}
          className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
        >
          <option value="">All Cities</option>
          <option value="london">London</option>
          <option value="manchester">Manchester</option>
        </select>
        <select
          value={country}
          onChange={(e) => setCountry(e.target.value)}
          className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
        >
          <option value="">All Countries</option>
          <option value="uk">UK</option>
          <option value="germany">Germany</option>
          {/* Add dynamically if needed */}
        </select>
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
        >
          <option value="">Sort By</option>
          <option value="name">Name (A-Z)</option>
          <option value="-name">Name (Z-A)</option>
          <option value="city">City</option>
          <option value="country">Country</option>
        </select>
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
        onUpdated={handleUpdateInList}
      />
    </section>
  );
}
