const $ = (selector) => document.querySelector(selector);
const tokenKey = 'helpflow.accessToken';
const emailKey = 'helpflow.email';

function setMessage(selector, message = '', error = false) {
  const node = $(selector); node.textContent = message; node.classList.toggle('error', error);
}
function openAuth(mode) {
  $('#auth-panel').classList.add('visible');
  $('#login-form').hidden = mode !== 'login'; $('#register-form').hidden = mode !== 'register';
  setMessage('#auth-message');
}
function authHeaders() { return { 'Authorization': `Bearer ${localStorage.getItem(tokenKey)}`, 'Content-Type': 'application/json' }; }
function friendlyError(data) { return data?.detail || data?.message || 'No fue posible completar la operación. Intenta de nuevo.'; }

async function loadTickets() {
  const list = $('#ticket-list'); list.innerHTML = '<p class="empty">Cargando tickets…</p>';
  const response = await fetch('/tickets', {headers: authHeaders()});
  if (response.status === 401 || response.status === 403) return logout(true);
  const tickets = await response.json();
  if (!tickets.length) { list.innerHTML = '<p class="empty">Aún no tienes solicitudes. Crea la primera desde el formulario.</p>'; return; }
  list.innerHTML = tickets.map(ticket => `<article class="ticket"><div class="ticket-top"><div><div class="ticket-title">${escapeHtml(ticket.title)}</div><span class="ticket-id">Ticket #${ticket.id}</span></div><span class="badge">${formatStatus(ticket.status)}</span></div><p class="ticket-meta">${ticket.category} · Prioridad ${ticket.priority} · SLA: ${new Date(ticket.slaDueAt).toLocaleString('es-CO')}</p></article>`).join('');
}
function formatStatus(value) { return value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase()); }
function escapeHtml(value) { const node = document.createElement('span'); node.textContent = value; return node.innerHTML; }
function showWorkspace() { $('#welcome').hidden = true; $('#app').hidden = false; $('#session-actions').hidden = true; $('#user-email').textContent = localStorage.getItem(emailKey) || 'usuario'; loadTickets(); }
function logout(expired = false) { localStorage.removeItem(tokenKey); localStorage.removeItem(emailKey); $('#app').hidden = true; $('#welcome').hidden = false; $('#session-actions').hidden = false; if (expired) openAuth('login'); }

document.querySelectorAll('[data-open]').forEach(button => button.addEventListener('click', () => openAuth(button.dataset.open)));
$('#show-login').addEventListener('click', () => openAuth('login')); $('#show-register').addEventListener('click', () => openAuth('register'));
$('#close-auth').addEventListener('click', () => $('#auth-panel').classList.remove('visible'));
$('#logout').addEventListener('click', () => logout()); $('#reload').addEventListener('click', loadTickets);

$('#register').addEventListener('submit', async event => {
  event.preventDefault(); setMessage('#auth-message', 'Creando tu cuenta…');
  const data = Object.fromEntries(new FormData(event.target));
  const response = await fetch('/auth/register', {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)});
  if (!response.ok) { setMessage('#auth-message', friendlyError(await response.json().catch(() => null)), true); return; }
  event.target.reset(); setMessage('#auth-message', 'Cuenta creada. Ahora inicia sesión.'); setTimeout(() => openAuth('login'), 850);
});
$('#login').addEventListener('submit', async event => {
  event.preventDefault(); setMessage('#auth-message', 'Validando acceso…');
  const data = Object.fromEntries(new FormData(event.target));
  const response = await fetch('/auth/login', {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)});
  if (!response.ok) { setMessage('#auth-message', friendlyError(await response.json().catch(() => null)), true); return; }
  const result = await response.json(); localStorage.setItem(tokenKey, result.accessToken); localStorage.setItem(emailKey, data.email); $('#auth-panel').classList.remove('visible'); showWorkspace();
});
$('#ticket-form').addEventListener('submit', async event => {
  event.preventDefault(); setMessage('#ticket-message', 'Enviando solicitud…');
  const data = Object.fromEntries(new FormData(event.target));
  const response = await fetch('/tickets', {method:'POST', headers:authHeaders(), body:JSON.stringify(data)});
  if (!response.ok) { setMessage('#ticket-message', friendlyError(await response.json().catch(() => null)), true); return; }
  event.target.reset(); setMessage('#ticket-message', 'Solicitud creada correctamente.'); loadTickets();
});
if (localStorage.getItem(tokenKey)) showWorkspace();
