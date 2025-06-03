import React, { useState, useEffect } from "react";
import {
  fetchClients,
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

  const load = async () => {
    setLoading(true);
    try {
      setClients(await fetchClients());
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

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
    setClients((cur) =>
      cur.map((c) => (c.id === updated.id ? updated : c))
    );

  /* UI ------------------------------------------------------------------ */

  if (loading) return <p className="p-6">Loading clients…</p>;
  if (error) return <p className="p-6 text-rose-400">Error: {error}</p>;

  return (
    <section className="space-y-8">
      {/* nav bar --------------------------------------------------------- */}
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

      {/* table ----------------------------------------------------------- */}
      <ClientTable
        clients={clients}
        onEdit={(c) => setEditTarget(c)}
        onDelete={handleDelete}
      />

      {/* modals ---------------------------------------------------------- */}
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
