/**
 * auth.js — Login & Register page logic
 */

const API_BASE = 'http://localhost:8080/api';

// ===== LOGIN =====
async function handleLogin(event) {
  event.preventDefault();
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();
  const errorEl = document.getElementById('loginError');
  const btnText = document.getElementById('btnText');
  const btnLoader = document.getElementById('btnLoader');

  // Show loading
  btnText.classList.add('hidden');
  btnLoader.classList.remove('hidden');
  errorEl.classList.add('hidden');

  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await res.json();

    if (data.success) {
      // Store credentials in session
      sessionStorage.setItem('tsms_user', JSON.stringify({ username, password }));
      window.location.href = 'dashboard.html';
    } else {
      errorEl.classList.remove('hidden');
    }
  } catch (err) {
    // If backend not running, allow demo login
    if (username === 'admin' && password === 'admin123') {
      sessionStorage.setItem('tsms_user', JSON.stringify({ username, password }));
      window.location.href = 'dashboard.html';
    } else {
      errorEl.textContent = '⚠️ Cannot connect to server. Use admin / admin123 for demo.';
      errorEl.classList.remove('hidden');
    }
  } finally {
    btnText.classList.remove('hidden');
    btnLoader.classList.add('hidden');
  }
}

// ===== REGISTER =====
async function handleRegister(event) {
  event.preventDefault();
  const fullName = document.getElementById('fullName').value.trim();
  const username = document.getElementById('regUsername').value.trim();
  const password = document.getElementById('regPassword').value.trim();
  const confirm = document.getElementById('confirmPassword').value.trim();
  const successEl = document.getElementById('regSuccess');
  const errorEl = document.getElementById('regError');

  successEl.classList.add('hidden');
  errorEl.classList.add('hidden');

  if (password !== confirm) {
    errorEl.textContent = '⚠️ Passwords do not match.';
    errorEl.classList.remove('hidden');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fullName, username, password })
    });

    if (res.ok) {
      successEl.classList.remove('hidden');
      setTimeout(() => window.location.href = 'login.html', 2000);
    } else {
      errorEl.textContent = '⚠️ Username already exists or registration failed.';
      errorEl.classList.remove('hidden');
    }
  } catch (err) {
    // Demo mode
    successEl.textContent = '✅ Demo mode: Account simulated! Redirecting...';
    successEl.classList.remove('hidden');
    setTimeout(() => window.location.href = 'login.html', 2000);
  }
}
