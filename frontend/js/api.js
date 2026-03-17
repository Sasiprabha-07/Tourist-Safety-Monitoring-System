/**
 * api.js — All backend API calls for TSMS
 * Base URL points to Spring Boot backend on port 8080
 */

const API_BASE = 'http://localhost:8080/api';

// ========== AUTH API ==========

/**
 * Login — calls POST /api/auth/login
 * Returns { success: true/false, message: '...' }
 */
async function apiLogin(username, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  return res.json();
}

// ========== TOURIST APIs ==========

/**
 * Get all tourists — GET /api/tourists
 */
async function apiGetAllTourists() {
  const res = await fetch(`${API_BASE}/tourists`, {
    headers: getAuthHeaders()
  });
  if (!res.ok) throw new Error('Failed to fetch tourists');
  return res.json();
}

/**
 * Add a tourist — POST /api/tourists
 */
async function apiAddTourist(tourist) {
  const res = await fetch(`${API_BASE}/tourists`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
    body: JSON.stringify(tourist)
  });
  if (!res.ok) throw new Error('Failed to add tourist');
  return res.json();
}

/**
 * Update tourist status — PUT /api/tourists/{id}
 */
async function apiUpdateTourist(id, data) {
  const res = await fetch(`${API_BASE}/tourists/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
    body: JSON.stringify(data)
  });
  if (!res.ok) throw new Error('Failed to update tourist');
  return res.json();
}

/**
 * Delete tourist — DELETE /api/tourists/{id}
 */
async function apiDeleteTourist(id) {
  const res = await fetch(`${API_BASE}/tourists/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  });
  if (!res.ok) throw new Error('Failed to delete tourist');
  return res.json();
}

// ========== HELPERS ==========

/** Returns auth headers if user token is stored */
function getAuthHeaders() {
  const user = sessionStorage.getItem('tsms_user');
  if (user) {
    const { username, password } = JSON.parse(user);
    // Basic auth header (matches Spring Security basic auth)
    const creds = btoa(`${username}:${password}`);
    return { 'Authorization': `Basic ${creds}` };
  }
  return {};
}

/** Check if user is logged in, redirect if not */
function requireAuth() {
  if (!sessionStorage.getItem('tsms_user')) {
    window.location.href = 'login.html';
  }
}
