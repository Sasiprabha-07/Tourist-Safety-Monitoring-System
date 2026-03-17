/**
 * dashboard.js — All dashboard logic: load, add, edit, delete tourists
 */

// ---- State ----
let allTourists = [];       // full list from backend
let filteredTourists = [];  // after search/filter
let deleteTargetId = null;  // for delete modal
let editTargetId = null;    // for edit modal

// ---- On page load ----
document.addEventListener('DOMContentLoaded', () => {
  requireAuth();

  // Show logged-in username
  const user = JSON.parse(sessionStorage.getItem('tsms_user') || '{}');
  document.getElementById('loggedUser').textContent = user.username || 'User';

  // Start clock
  updateClock();
  setInterval(updateClock, 1000);

  // Load tourists
  loadAllTourists();
});

// ---- Live Clock ----
function updateClock() {
  const el = document.getElementById('currentTime');
  if (el) el.textContent = new Date().toLocaleTimeString();
}

// ---- Navigation ----
function showSection(name, linkEl) {
  // Hide all sections
  document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
  // Show target
  document.getElementById('section-' + name).classList.add('active');
  // Update nav active
  document.querySelectorAll('.nav-item').forEach(a => a.classList.remove('active'));
  if (linkEl) linkEl.classList.add('active');
  // Update title
  const titles = { dashboard: 'Dashboard', tourists: 'All Tourists', add: 'Add Tourist', about: 'About' };
  document.getElementById('pageTitle').textContent = titles[name] || 'Dashboard';
}

function toggleSidebar() {
  document.getElementById('sidebar').classList.toggle('open');
}

// ---- Load All Tourists ----
async function loadAllTourists() {
  try {
    allTourists = await apiGetAllTourists();
  } catch (err) {
    // Demo data if backend is not running
    console.warn('Backend not reachable, using demo data');
    allTourists = getDemoData();
  }
  filteredTourists = [...allTourists];
  updateStats();
  renderDashTable();
  renderMainTable();
}

// ---- Demo Data (if backend offline) ----
function getDemoData() {
  return [
    { id: 1, name: 'Rahul Sharma', location: 'Manali, India', status: 'Safe' },
    { id: 2, name: 'Emily Davis', location: 'Goa, India', status: 'In Danger' },
    { id: 3, name: 'Chen Wei', location: 'Ooty, India', status: 'Missing' },
    { id: 4, name: 'Priya Nair', location: 'Kerala, India', status: 'Safe' },
    { id: 5, name: 'James Wilson', location: 'Rajasthan, India', status: 'Safe' },
  ];
}

// ---- Stats ----
function updateStats() {
  const total = allTourists.length;
  const safe = allTourists.filter(t => t.status === 'Safe').length;
  const danger = allTourists.filter(t => t.status === 'In Danger').length;
  const missing = allTourists.filter(t => t.status === 'Missing').length;

  document.getElementById('statTotal').textContent = total;
  document.getElementById('statSafe').textContent = safe;
  document.getElementById('statDanger').textContent = danger;
  document.getElementById('statMissing').textContent = missing;

  // Progress bars
  if (total > 0) {
    document.getElementById('safeBar').style.width = (safe / total * 100) + '%';
    document.getElementById('dangerBar').style.width = (danger / total * 100) + '%';
    document.getElementById('missingBar').style.width = (missing / total * 100) + '%';
  }
}

// ---- Badge HTML ----
function getBadge(status) {
  const map = {
    'Safe': '<span class="badge badge-safe">✅ Safe</span>',
    'In Danger': '<span class="badge badge-danger">⚠️ In Danger</span>',
    'Missing': '<span class="badge badge-missing">🔍 Missing</span>'
  };
  return map[status] || `<span class="badge">${status}</span>`;
}

// ---- Render Dashboard Table (last 5) ----
function renderDashTable() {
  const tbody = document.getElementById('dashTableBody');
  const recent = allTourists.slice(-5).reverse();
  if (recent.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-row">No tourists yet. Add one!</td></tr>';
    return;
  }
  tbody.innerHTML = recent.map((t, i) => `
    <tr>
      <td>${i + 1}</td>
      <td><strong>${escHtml(t.name)}</strong></td>
      <td>${escHtml(t.location)}</td>
      <td>${getBadge(t.status)}</td>
      <td>
        <div class="action-btns">
          <button class="act-btn act-edit" onclick="openEditModal(${t.id}, '${escHtml(t.name)}', '${t.status}')">Edit</button>
          <button class="act-btn act-delete" onclick="openDeleteModal(${t.id})">Delete</button>
        </div>
      </td>
    </tr>`).join('');
}

// ---- Render Main Table ----
function renderMainTable() {
  const tbody = document.getElementById('mainTableBody');
  if (filteredTourists.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-row">No records found.</td></tr>';
    return;
  }
  tbody.innerHTML = filteredTourists.map((t, i) => `
    <tr>
      <td>${i + 1}</td>
      <td><strong>${escHtml(t.name)}</strong></td>
      <td>${escHtml(t.location)}</td>
      <td>${getBadge(t.status)}</td>
      <td>
        <div class="action-btns">
          <button class="act-btn act-edit" onclick="openEditModal(${t.id}, '${escHtml(t.name)}', '${t.status}')">✏️ Edit</button>
          <button class="act-btn act-delete" onclick="openDeleteModal(${t.id})">🗑️ Delete</button>
        </div>
      </td>
    </tr>`).join('');
}

// ---- Search / Filter ----
function filterTable() {
  const search = document.getElementById('searchInput').value.toLowerCase();
  const status = document.getElementById('filterStatus').value;
  filteredTourists = allTourists.filter(t => {
    const matchSearch = t.name.toLowerCase().includes(search) || t.location.toLowerCase().includes(search);
    const matchStatus = status === '' || t.status === status;
    return matchSearch && matchStatus;
  });
  renderMainTable();
}

// ---- Add / Edit Tourist Form ----
async function submitTouristForm(event) {
  event.preventDefault();
  const name = document.getElementById('touristName').value.trim();
  const location = document.getElementById('touristLocation').value.trim();
  const status = document.getElementById('touristStatus').value;
  const editId = document.getElementById('editId').value;
  const msgEl = document.getElementById('formMsg');
  const btn = document.getElementById('submitBtn');

  btn.disabled = true;
  btn.textContent = editId ? 'Updating...' : 'Adding...';
  msgEl.classList.add('hidden');

  try {
    if (editId) {
      await apiUpdateTourist(editId, { name, location, status });
      showMsg(msgEl, '✅ Tourist updated successfully!', 'success');
    } else {
      await apiAddTourist({ name, location, status });
      showMsg(msgEl, '✅ Tourist added successfully!', 'success');
    }
    await loadAllTourists();
    setTimeout(() => resetForm(), 1500);
    showToast(editId ? 'Tourist updated!' : 'Tourist added!', 'success');
  } catch (err) {
    // Demo fallback
    const id = Date.now();
    if (editId) {
      const idx = allTourists.findIndex(t => t.id == editId);
      if (idx > -1) allTourists[idx] = { id: Number(editId), name, location, status };
    } else {
      allTourists.push({ id, name, location, status });
    }
    filteredTourists = [...allTourists];
    updateStats(); renderDashTable(); renderMainTable();
    showMsg(msgEl, editId ? '✅ Updated (demo mode).' : '✅ Added (demo mode).', 'success');
    showToast(editId ? 'Tourist updated!' : 'Tourist added!', 'success');
    setTimeout(() => resetForm(), 1500);
  } finally {
    btn.disabled = false;
    btn.textContent = editId ? 'Update Tourist' : 'Add Tourist';
  }
}

function resetForm() {
  document.getElementById('touristForm').reset();
  document.getElementById('editId').value = '';
  document.getElementById('formTitle').textContent = 'Add New Tourist';
  document.getElementById('submitBtn').textContent = 'Add Tourist';
  document.getElementById('formMsg').classList.add('hidden');
}

// ---- Delete Modal ----
function openDeleteModal(id) {
  deleteTargetId = id;
  document.getElementById('deleteModal').classList.remove('hidden');
  document.getElementById('confirmDeleteBtn').onclick = confirmDelete;
}
async function confirmDelete() {
  if (!deleteTargetId) return;
  try {
    await apiDeleteTourist(deleteTargetId);
  } catch {
    // Demo fallback
    allTourists = allTourists.filter(t => t.id !== deleteTargetId);
  }
  closeModal();
  await loadAllTourists();
  showToast('Tourist deleted.', 'success');
  deleteTargetId = null;
}
function closeModal() {
  document.getElementById('deleteModal').classList.add('hidden');
}

// ---- Edit Modal ----
function openEditModal(id, name, status) {
  editTargetId = id;
  document.getElementById('editTouristName').textContent = `Tourist: ${name}`;
  document.getElementById('editStatus').value = status;
  document.getElementById('editModal').classList.remove('hidden');
}
async function confirmUpdate() {
  if (!editTargetId) return;
  const newStatus = document.getElementById('editStatus').value;
  try {
    await apiUpdateTourist(editTargetId, { status: newStatus });
  } catch {
    const t = allTourists.find(t => t.id === editTargetId);
    if (t) t.status = newStatus;
  }
  closeEditModal();
  await loadAllTourists();
  showToast('Status updated!', 'success');
  editTargetId = null;
}
function closeEditModal() {
  document.getElementById('editModal').classList.add('hidden');
}

// ---- Logout ----
function logout() {
  sessionStorage.removeItem('tsms_user');
  window.location.href = 'login.html';
}

// ---- Toast ----
function showToast(msg, type = 'success') {
  const el = document.getElementById('toast');
  el.textContent = (type === 'success' ? '✅ ' : '❌ ') + msg;
  el.className = `toast toast-${type}`;
  el.classList.remove('hidden');
  setTimeout(() => el.classList.add('hidden'), 3000);
}

// ---- Form message helper ----
function showMsg(el, msg, type) {
  el.textContent = msg;
  el.className = `form-msg ${type}`;
  el.classList.remove('hidden');
  setTimeout(() => el.classList.add('hidden'), 3500);
}

// ---- Escape HTML ----
function escHtml(str) {
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
