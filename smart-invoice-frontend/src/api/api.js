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
      data: await response.json().catch(() => null) // Handle empty responses
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
    return {
      ok: response.ok,
      data: await response.json().catch(() => null)
    };
  } catch (error) {
    console.error('API getCurrentUser error:', error);
    return {
      ok: false,
      error: 'Network error'
    };
  }
};

export const logout = async () => {
  try {
    const response = await fetch(`${API_BASE}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
    });
    return response.ok;
  } catch (error) {
    console.error('API logout error:', error);
    return false;
  }
};