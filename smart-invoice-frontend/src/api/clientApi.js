const API_BASE = "http://localhost:8080";

export const fetchClients = async () => {
  try {
    const response = await fetch(`${API_BASE}/api/clients`, {
      credentials: 'include'
    });
    if (!response.ok) throw new Error(`Failed to fetch clients: ${response.status}`);
    const json = await response.json();
    return json;
  } catch (error) {
    console.error('Error fetching clients:', error);
    throw error;
  }
};

export async function fetchClientsWithFilters(queryString) {
  const res = await fetch(`http://localhost:8080/api/clients/filter?${queryString}`, {
    credentials: 'include',
  });
  if (!res.ok) throw new Error("Failed to fetch clients");
  return res.json();
}

export const createClient = async (clientData) => {
  try {
    const response = await fetch(`${API_BASE}/api/clients`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(clientData)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create client');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error creating client:', error);
    throw error;
  }
};

export const updateClient = async (id, clientData) => {
  try {
    const response = await fetch(`${API_BASE}/api/clients/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(clientData)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to update client');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error updating client:', error);
    throw error;
  }
};

export const deleteClient = async (id) => {
  try {
    const response = await fetch(`${API_BASE}/api/clients/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to delete client');
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting client:', error);
    throw error;
  }
};