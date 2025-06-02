import React, { useState, useEffect } from 'react';
import { fetchClients } from '../api/clientApi';
import ClientTable from '../components/clients/ClientTable';
import ClientFormModal from '../components/clients/ClientFormModal';
import { useAuth } from '../contexts/AuthContext';

export default function Clients() {
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    console.log('Starting to load clients...'); // Debug 1
    const loadClients = async () => {
      try {
        console.log('Making API call...'); // Debug 2
        const data = await fetchClients();
        if (!data || !Array.isArray(data)) throw new Error("Invalid client data");
        console.log('Received data:', data); // Debug 3
        setClients(data);
      } catch (err) {
        console.error('Error loading clients:', err); // Debug 4
        setError(err.message);
      } finally {
        console.log('Finished loading attempt'); // Debug 5
        setLoading(false);
      }
    };
    loadClients();
  }, []);

  if (loading) return <div className="p-6">Loading clients...</div>;
  if (error) return <div className="p-6 text-red-500">Error: {error}</div>;

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Client Management</h1>
        {user && (
          <button
            onClick={() => setIsModalOpen(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Add New Client
          </button>
        )}
      </div>

      <ClientTable clients={clients} />
      
      <ClientFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onClientCreated={(newClient) => setClients([...clients, newClient])}
      />
    </div>
  );
}