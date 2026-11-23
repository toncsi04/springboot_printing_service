// Alkalmazás állapota (memóriában tárolt HTTP Basic hitelesítő fejléc)
let authHeader = null;
let currentUsername = null;

// Segédfüggvény az értesítések megjelenítéséhez
function showNotification(message, type = 'success') {
    const bar = document.getElementById('notification-bar');
    bar.textContent = message;
    bar.className = `notification ${type}`;
    setTimeout(() => {
        bar.classList.add('hidden');
    }, 5000);
}

// Egységes API hívó függvény (kezeli a Basic Auth-ot és a Spring ProblemDetail hibákat)
async function apiFetch(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (authHeader) {
        headers['Authorization'] = authHeader;
    }

    const response = await fetch(url, { ...options, headers });

    // Ha hiba történt (pl. 401, 403, 404, 400), kiolvassuk a ProblemDetail JSON-t
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

    // 204 No Content (pl. DELETE) esetén nincs JSON törzs
    if (response.status === 204) {
        return null;
    }

    return response.json();
}

// Fülek (Bejelentkezés / Regisztráció) váltása
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById(btn.dataset.tab).classList.add('active');
    });
});

// 1. REGISZTRÁCIÓ (POST /api/users - nyilvános végpont)
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
        e.target.reset();
        document.querySelector('[data-tab="login-panel"]').click();
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

// 2. BEJELENTKEZÉS ÉS ADATOK BETÖLTÉSE
document.getElementById('login-panel').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    // Basic Auth Base64 kódolása
    authHeader = 'Basic ' + btoa(`${username}:${password}`);
    currentUsername = username;

    try {
        await loadUsersAndMaterials();
        updateAuthUI(true);
        showNotification(`Üdvözlünk, ${username}! Adatok sikeresen betöltve.`, 'success');
    } catch (err) {
        authHeader = null;
        currentUsername = null;
        updateAuthUI(false);
        showNotification(err.message, 'error');
    }
});

// Kijelentkezés
document.getElementById('logout-btn').addEventListener('click', () => {
    authHeader = null;
    currentUsername = null;
    updateAuthUI(false);
    document.getElementById('materials-tbody').innerHTML =
        '<tr><td colspan="4" class="empty-row">Jelentkezz be az anyagok megtekintéséhez!</td></tr>';
    document.getElementById('quote-user-select').innerHTML = '<option value="">-- Jelentkezz be --</option>';
    document.getElementById('quote-material-select').innerHTML = '<option value="">-- Válassz anyagot --</option>';
    showNotification('Sikeresen kijelentkeztél.', 'success');
});

function updateAuthUI(isLoggedIn) {
    const badge = document.getElementById('auth-status');
    const logoutBtn = document.getElementById('logout-btn');
    if (isLoggedIn) {
        badge.textContent = `Bejelentkezve: ${currentUsername}`;
        badge.className = 'auth-badge connected';
        logoutBtn.classList.remove('hidden');
    } else {
        badge.textContent = 'Nincs bejelentkezve';
        badge.className = 'auth-badge disconnected';
        logoutBtn.classList.add('hidden');
    }
}

// Felhasználók és Anyagok betöltése a legördülő listákba és a táblázatba
async function loadUsersAndMaterials() {
    const [users, materials] = await Promise.all([
        apiFetch('/api/users'),
        apiFetch('/api/materials')
    ]);

    // Felhasználó legördülő lista feltöltése
    //const userSelect = document.getElementById('quote-user-select');
    //userSelect.innerHTML = users.map(u =>
    //    `<option value="${u.id}" ${u.felhasznalonev === currentUsername ? 'selected' : ''}>
    //        #${u.id} – ${u.felhasznalonev} (${u.email})
    //    </option>`
    //).join('');

    renderMaterials(materials);
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

// 3. ÚJ NYOMTATÓANYAG FELVÉTELE (POST /api/materials)
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

// Anyag törlése (DELETE /api/materials/{id})
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

document.getElementById('refresh-materials-btn').addEventListener('click', async () => {
    try {
        const materials = await apiFetch('/api/materials');
        renderMaterials(materials);
        showNotification('Anyaglista frissítve.', 'success');
    } catch (err) {
        showNotification(err.message, 'error');
    }
});

// 4. ÁRAJÁNLAT KALKULÁCIÓ ÉS MENTÉS (POST /api/quotes/calculate)
document.getElementById('quote-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        userId: parseInt(document.getElementById('quote-user-select').value, 10),
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
    } catch (err) {
        showNotification(err.message, 'error');
    }
});