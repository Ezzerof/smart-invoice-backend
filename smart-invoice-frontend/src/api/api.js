
const API_BASE = "http://localhost:8080";

export const login = async (username, password) => {
  try {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ username, password }),
      credentials: "include",
    });
    
    
    return {
      ok: response.ok,
      status: response.status,
      data: await response.json().catch(() => null)
    };
  } catch (error) {
    console.error('API login error:', error);
    return {
      ok: false,
      error: 'Network error'
    };
  }
};

export const getCurrentUser = async () => {
  try {
    const response = await fetch(`${API_BASE}/api/auth/me`, {
      credentials: "include",
    });

    const isJson = response.headers
    .get("content-type")
    ?.includes("application/json");

    if (response.status === 401) {
      return { ok: false, status: 401 };
    }
    
    if (!response.ok) throw new Error('Failed to fetch user');

    return {
      ok: true,
      data: isJson ? await response.json() : null,
    };
  } catch (error) {
    console.error('API Error:', error);
    return { ok: false, error: error.message };
  }
};

export const logout = async () => {
  try {
    const response = await fetch(`${API_BASE}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`Logout failed (${response.status})`);
    }

    return true;
  } catch (error) {
    console.error('API logout error:', error);
    return false;
  }
};