let authHeader = sessionStorage.getItem('authHeader');
let currentUsername = sessionStorage.getItem('currentUsername');
let isAdmin = false;
function showNotification(message, type = 'success') {
    const bar = document.getElementById('notification-bar');
    bar.textContent = message;
    bar.className = `notification ${type}`;
    setTimeout(() => {
        bar.classList.add('hidden');
    }, 5000);
}

async function apiFetch(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (authHeader) {
        headers['Authorization'] = authHeader;
    }

    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error('Hibás felhasználónév vagy jelszó, illetve nincs bejelentkezve!');
        }
        if (response.status === 403) {
            throw new Error('Nincs jogosultságod ehhez a művelethez (ADMIN jog szükséges)!');
        }
        try {
            const problem = await response.json();
            throw new Error(problem.detail || problem.title || `Szerverhiba (${response.status})`);
        } catch (e) {
            if (e.message) throw e;
            throw new Error(`HTTP hiba: ${response.status}`);
        }
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

// Fülek váltása
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById(btn.dataset.tab).classList.add('active');
    });
});

// 1. REGISZTRÁCIÓ
document.getElementById('register-panel').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        felhasznalonev: document.getElementById('reg-username').value.trim(),
        email: document.getElementById('reg-email').value.trim(),
        jelszo: document.getElementById('reg-password').value
    };

    try {
        const created = await apiFetch('/api/users', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showNotification(`Sikeres regisztráció (${created.felhasznalonev})! Most már bejelentkezhetsz.`, 'success');
        sessionStorage.setItem('authHeader', authHeader);
        sessionStorage.setItem('currentUsername', currentUsername);
        e.target.reset();
        document.querySelector('[data-tab="login-panel"]').click();
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

// 2. BEJELENTKEZÉS
document.getElementById('login-panel').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    authHeader = 'Basic ' + btoa(`${username}:${password}`);
    currentUsername = username;

    try {
        await loadAllData();
        updateAuthUI(true);
        showNotification(`Üdvözlünk, ${username}! (${isAdmin ? 'ADMIN' : 'USER'} jogosultság)`, 'success');
        sessionStorage.setItem('authHeader', authHeader);
        sessionStorage.setItem('currentUsername', currentUsername);
    } catch (err) {
        authHeader = null;
        currentUsername = null;
        setAdminPanelsVisibility(false);
        updateAuthUI(false);
        showNotification(err.message, 'error');
    }
});

// Kijelentkezés
document.getElementById('logout-btn').addEventListener('click', () => {
    authHeader = null;
    currentUsername = null;
    setAdminPanelsVisibility(false);
    updateAuthUI(false);
    document.getElementById('materials-tbody').innerHTML =
        '<tr><td colspan="4" class="empty-row">Jelentkezz be az anyagok megtekintéséhez!</td></tr>';
    document.getElementById('quote-material-select').innerHTML = '<option value="">-- Válassz anyagot --</option>';
    document.getElementById('quote-result-box').classList.add('hidden');
    showNotification('Sikeresen kijelentkeztél.', 'success');
    sessionStorage.removeItem('authHeader');
    sessionStorage.removeItem('currentUsername');
});

function updateAuthUI(isLoggedIn) {
    const badge = document.getElementById('auth-status');
    const logoutBtn = document.getElementById('logout-btn');
    if (isLoggedIn) {
        badge.textContent = `Bejelentkezve: ${currentUsername} ${isAdmin ? '(ADMIN)' : ''}`;
        badge.className = 'auth-badge connected';
        logoutBtn.classList.remove('hidden');
    } else {
        badge.textContent = 'Nincs bejelentkezve';
        badge.className = 'auth-badge disconnected';
        logoutBtn.classList.add('hidden');
    }
}

// Kifejezetten a 4. és 5. szekciót jeleníti meg vagy rejti el
function setAdminPanelsVisibility(show) {
    isAdmin = show;
    const usersSec = document.getElementById('admin-users-section');
    const quotesSec = document.getElementById('admin-quotes-section');
    if (show) {
        usersSec.classList.remove('hidden');
        quotesSec.classList.remove('hidden');
    } else {
        usersSec.classList.add('hidden');
        quotesSec.classList.add('hidden');
    }
}

async function loadAllData() {
    // Anyagok betöltése (minden bejelentkezett felhasználónak elérhető)
    const materials = await apiFetch('/api/materials');
    renderMaterials(materials);

    // Megpróbáljuk betölteni az admin végpontokat (/api/users és /api/quotes)
    await loadAdminData();
}

async function loadAdminData() {
    try {
        const users = await apiFetch('/api/users');
        setAdminPanelsVisibility(true);
        renderUsers(users);

        try {
            const quotes = await apiFetch('/api/quotes');
            renderQuotes(quotes);
        } catch (quoteErr) {
            document.getElementById('quotes-tbody').innerHTML =
                `<tr><td colspan="6" class="empty-row">Nem sikerült betölteni az árajánlatokat (${quoteErr.message})</td></tr>`;
        }
    } catch (e) {
        // Sima USER esetén a /api/users 403-at dob -> az admin panelek rejtve maradnak
        setAdminPanelsVisibility(false);
    }
}

function renderMaterials(materials) {
    const matSelect = document.getElementById('quote-material-select');
    const tbody = document.getElementById('materials-tbody');

    if (!materials || materials.length === 0) {
        matSelect.innerHTML = '<option value="">-- Nincs még anyag rögzítve --</option>';
        tbody.innerHTML = '<tr><td colspan="4" class="empty-row">Még nincs nyomtatóanyag az adatbázisban.</td></tr>';
        return;
    }

    matSelect.innerHTML = materials.map(m =>
        `<option value="${m.id}">${m.nev} (${m.grammAr} Ft/g)</option>`
    ).join('');

    tbody.innerHTML = materials.map(m => `
        <tr>
            <td>#${m.id}</td>
            <td><strong>${m.nev}</strong></td>
            <td>${m.grammAr} Ft / g</td>
            <td>
                <button type="button" class="btn btn-danger" onclick="deleteMaterial(${m.id})">Törlés</button>
            </td>
        </tr>
    `).join('');
}

function renderUsers(users) {
    const tbody = document.getElementById('users-tbody');
    if (!users || users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty-row">Nincs regisztrált felhasználó.</td></tr>';
        return;
    }

    tbody.innerHTML = users.map(u => `
        <tr>
            <td>#${u.id}</td>
            <td><strong>${u.felhasznalonev}</strong></td>
            <td>${u.email}</td>
            <td>
                ${u.felhasznalonev !== currentUsername
        ? `<button type="button" class="btn btn-danger" onclick="deleteUser(${u.id})">Törlés</button>`
        : '<span class="tag">Saját fiók</span>'}
            </td>
        </tr>
    `).join('');
}

function renderQuotes(quotes) {
    const tbody = document.getElementById('quotes-tbody');
    if (!quotes || quotes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-row">Még nincs rögzített árajánlat.</td></tr>';
        return;
    }

    tbody.innerHTML = quotes.map(q => {
        const megrendelo = q.user?.felhasznalonev || q.felhasznalonev || (q.userId ? `User #${q.userId}` : 'Ismeretlen');
        const anyag = q.material?.nev || q.materialNev || (q.materialId ? `Material #${q.materialId}` : '-');
        const osszeg = q.vegosszeg ?? q.ar ?? 0;

        return `
            <tr>
                <td>#${q.id}</td>
                <td><strong>${megrendelo}</strong></td>
                <td>${anyag}</td>
                <td>${q.suly} g</td>
                <td><strong>${Number(osszeg).toLocaleString('hu-HU')} Ft</strong></td>
                <td>
                    <button type="button" class="btn btn-danger" onclick="deleteQuote(${q.id})">Törlés</button>
                </td>
            </tr>
        `;
    }).join('');
}

// 3. ÚJ NYOMTATÓANYAG FELVÉTELE
document.getElementById('material-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        nev: document.getElementById('mat-name').value.trim(),
        grammAr: parseFloat(document.getElementById('mat-price').value)
    };

    try {
        await apiFetch('/api/materials', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showNotification(`"${payload.nev}" sikeresen elmentve!`, 'success');
        e.target.reset();
        const materials = await apiFetch('/api/materials');
        renderMaterials(materials);
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

window.deleteMaterial = async function(id) {
    try {
        await apiFetch(`/api/materials/${id}`, { method: 'DELETE' });
        showNotification(`A #${id} azonosítójú anyag törölve lett.`, 'success');
        const materials = await apiFetch('/api/materials');
        renderMaterials(materials);
    } catch (err) {
        showNotification(err.message, 'error');
    }
};

window.deleteUser = async function(id) {
    try {
        await apiFetch(`/api/users/${id}`, { method: 'DELETE' });
        showNotification(`A #${id} azonosítójú felhasználó törölve lett.`, 'success');
        await loadAdminData();
    } catch (err) {
        showNotification(err.message, 'error');
    }
};

window.deleteQuote = async function(id) {
    try {
        await apiFetch(`/api/quotes/${id}`, { method: 'DELETE' });
        showNotification(`A #${id} azonosítójú árajánlat törölve lett.`, 'success');
        await loadAdminData();
    } catch (err) {
        showNotification(err.message, 'error');
    }
};

document.getElementById('refresh-materials-btn').addEventListener('click', async () => {
    try {
        const materials = await apiFetch('/api/materials');
        renderMaterials(materials);
        showNotification('Anyaglista frissítve.', 'success');
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

document.getElementById('refresh-admin-btn').addEventListener('click', async () => {
    await loadAdminData();
    showNotification('Admin adatok frissítve.', 'success');
});

// 4. ÁRAJÁNLAT KALKULÁCIÓ
document.getElementById('quote-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        materialId: parseInt(document.getElementById('quote-material-select').value, 10),
        sulyGrammban: parseInt(document.getElementById('quote-weight').value, 10)
    };

    try {
        const price = await apiFetch('/api/quotes/calculate', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        const resultBox = document.getElementById('quote-result-box');
        document.getElementById('quote-price-value').textContent = `${price.toLocaleString('hu-HU')} Ft`;
        resultBox.classList.remove('hidden');
        showNotification('Árajánlat sikeresen kiszámítva és elmentve!', 'success');

        if (isAdmin) {
            await loadAdminData();
        }
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

// Oldalbetöltéskor / F5 frissítéskor automatikus visszaléptetés
window.addEventListener('DOMContentLoaded', async () => {
    if (authHeader && currentUsername) {
        try {
            await loadAllData();
            updateAuthUI(true);
        } catch (e) {
            sessionStorage.removeItem('authHeader');
            sessionStorage.removeItem('currentUsername');
            authHeader = null;
            currentUsername = null;
            updateAuthUI(false);
        }
    }
});