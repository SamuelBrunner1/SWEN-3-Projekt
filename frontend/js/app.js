// ---- API base --------------------------------------------------------------
const API_BASE = ""; // nginx proxy / same origin

// ---- Token helpers ---------------------------------------------------------
function getToken() {
    return localStorage.getItem("token");
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function clearToken() {
    localStorage.removeItem("token");
}

function isLoggedIn() {
    return !!getToken();
}

// ---- Navbar include --------------------------------------------------------
async function loadNavbar() {
    const host = document.getElementById("navbar");
    if (!host) return;

    try {
        const res = await fetch("navbar.html", { cache: "no-store" });
        host.innerHTML = await res.text();

        // Fill auth area
        const authArea = document.getElementById("nav-auth-area");
        if (!authArea) return;

        if (isLoggedIn()) {
            authArea.innerHTML = `
                <a class="btn btn-outline-light btn-sm" href="index.html">Dashboard</a>
                <button class="btn btn-light btn-sm" id="logoutBtn">Logout</button>
            `;
            document.getElementById("logoutBtn").addEventListener("click", () => {
                clearToken();
                window.location.href = "login.html";
            });
        } else {
            authArea.innerHTML = `
                <a class="btn btn-outline-light btn-sm" href="login.html">Login</a>
                <a class="btn btn-light btn-sm" href="register.html">Registrieren</a>
            `;
        }
    } catch (e) {
        console.error("Navbar konnte nicht geladen werden:", e);
    }
}

// ---- Fetch helper with JWT -------------------------------------------------
async function apiFetch(path, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = getToken();
    if (token) headers.set("Authorization", `Bearer ${token}`);

    // Only set JSON headers when body is plain object
    if (options.json) {
        headers.set("Content-Type", "application/json");
        options.body = JSON.stringify(options.json);
        delete options.json;
    }

    const res = await fetch(API_BASE + path, { ...options, headers });

    if (res.status === 401 || res.status === 403) {
        // if user is on protected page, send them to login
        // (avoid loop on login/register)
        const page = (location.pathname.split("/").pop() || "").toLowerCase();
        if (!["login.html", "register.html"].includes(page)) {
            clearToken();
            window.location.href = "login.html";
        }
    }
    return res;
}

// ---- Helpers ---------------------------------------------------------------
async function parseErrorResponse(response) {
    try {
        const ct = response.headers.get("content-type") || "";
        if (ct.includes("application/json")) {
            const err = await response.json();
            return err.error || err.message || JSON.stringify(err);
        }
        return await response.text();
    } catch {
        return "Unbekannter Fehler beim Verarbeiten der Antwort.";
    }
}

function escapeHtml(s) {
    return (s || "").replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}

// ---- Auth: login/register --------------------------------------------------
async function handleLogin(e) {
    e.preventDefault();
    const msg = document.getElementById("loginMsg");
    msg.className = "small";
    msg.textContent = "";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    try {
        const res = await fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!res.ok) throw new Error(await parseErrorResponse(res));
        const data = await res.json();

        if (!data || !data.token) throw new Error("Kein Token erhalten.");
        setToken(data.token);

        window.location.href = "index.html";
    } catch (err) {
        msg.className = "small text-danger";
        msg.textContent = "Login fehlgeschlagen: " + err.message;
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const msg = document.getElementById("registerMsg");
    msg.className = "small";
    msg.textContent = "";

    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value;

    try {
        const res = await fetch("/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!res.ok) throw new Error(await parseErrorResponse(res));

        msg.className = "small text-success";
        msg.textContent = "Registrierung erfolgreich. Du kannst dich jetzt einloggen.";
        setTimeout(() => window.location.href = "login.html", 600);
    } catch (err) {
        msg.className = "small text-danger";
        msg.textContent = "Registrierung fehlgeschlagen: " + err.message;
    }
}

// ---- Guard for protected pages --------------------------------------------
function protectPage() {
    const page = (location.pathname.split("/").pop() || "").toLowerCase();
    const isPublic = ["login.html", "register.html"].includes(page);

    if (!isPublic && !isLoggedIn()) {
        window.location.href = "login.html";
    }
}

// ---- Dokumente laden -------------------------------------------------------
async function loadDocuments() {
    const container = document.getElementById("dokumente");
    if (!container) return;

    container.innerHTML = `<div class="alert alert-secondary">Lade Dokumente...</div>`;

    try {
        const response = await apiFetch("/api/dokumente");
        if (!response.ok) throw new Error(await parseErrorResponse(response));

        const data = await response.json();
        if (!data || data.length === 0) {
            container.innerHTML = `<div class="alert alert-info">Keine Dokumente vorhanden.</div>`;
            return;
        }

        container.innerHTML = data.map(doc => `
          <div class="p-3 border rounded mb-2 bg-white shadow-sm">
            <div class="d-flex justify-content-between align-items-center">
              <div class="me-3">
                <h5 class="mb-1">
                  <a href="detail.html?id=${doc.id}" class="text-decoration-none text-dark fw-bold">
                    ${escapeHtml(doc.titel ?? "(ohne Titel)")}
                  </a>
                </h5>
                <small class="text-muted">ID: ${doc.id}</small><br>
                <small class="text-muted">Key: ${escapeHtml(doc.dateiname ?? "(kein Key)")}
                </small>
              </div>
              <div class="btn-group">
                <a href="detail.html?id=${doc.id}" class="btn btn-sm btn-outline-primary">Öffnen</a>
                <button class="btn btn-sm btn-outline-danger" data-del="${doc.id}">Löschen</button>
              </div>
            </div>
          </div>
        `).join("");

        container.querySelectorAll("button[data-del]").forEach(btn => {
            btn.addEventListener("click", () => deleteDocument(btn.getAttribute("data-del")));
        });

    } catch (error) {
        console.error("Ladefehler:", error);
        container.innerHTML = `<div class="alert alert-danger">Fehler beim Laden der Dokumente: ${escapeHtml(error.message)}</div>`;
    }
}

// ---- Dokument löschen ------------------------------------------------------
async function deleteDocument(id) {
    if (!confirm(`Dokument #${id} wirklich löschen?`)) return;

    try {
        const res = await apiFetch(`/api/dokumente/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error(await parseErrorResponse(res));

        alert(`Dokument #${id} gelöscht.`);
        loadDocuments();
    } catch (error) {
        console.error("Fehler beim Löschen:", error);
        alert("Fehler beim Löschen: " + error.message);
    }
}

// ---- Datei-Upload (multipart -> /api/upload) --------------------------------
function wireUpload() {
    const uploadForm = document.getElementById("uploadForm");
    if (!uploadForm) return;

    uploadForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const fileInput = document.getElementById("file");
        const messageBox = document.getElementById("uploadMessage");

        messageBox.className = "";
        messageBox.textContent = "";

        const file = fileInput.files[0];
        if (!file) {
            messageBox.className = "text-danger";
            messageBox.textContent = "Bitte eine PDF-Datei wählen.";
            return;
        }
        if (!(file.type || "").toLowerCase().includes("pdf")) {
            messageBox.className = "text-danger";
            messageBox.textContent = "Nur PDFs werden akzeptiert.";
            return;
        }

        const fd = new FormData();
        fd.append("file", file);

        try {
            const res = await apiFetch("/api/upload", { method: "POST", body: fd });
            if (!res.ok) throw new Error(await parseErrorResponse(res));

            const id = await res.json(); // Backend gibt Long-ID
            messageBox.className = "text-success";
            messageBox.textContent = `Upload erfolgreich (ID: ${id}).`;

            fileInput.value = "";
            await loadDocuments();
        } catch (error) {
            console.error("Fehler beim Upload:", error);
            messageBox.className = "text-danger";
            messageBox.textContent = "Fehler beim Hochladen: " + error.message;
        }
    });
}

// ---- Detail page wiring (optional) -----------------------------------------
async function loadDetailPageIfPresent() {
    const titleEl = document.getElementById("detail-title");
    if (!titleEl) return; // not detail page

    const idEl = document.getElementById("detail-id");
    const keyEl = document.getElementById("detail-key");
    const summaryEl = document.getElementById("detail-summary");
    const deleteButton = document.getElementById("deleteButton");

    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) {
        titleEl.innerText = "Fehler";
        idEl.innerText = "–";
        keyEl.innerText = "Keine Dokument-ID angegeben.";
        summaryEl.innerText = "-";
        return;
    }

    async function load() {
        try {
            const response = await apiFetch(`/api/dokumente/${id}`);
            if (!response.ok) throw new Error(await parseErrorResponse(response));

            const doc = await response.json();
            titleEl.innerText = doc.titel ?? "(ohne Titel)";
            idEl.innerText = doc.id ?? "-";
            keyEl.innerText = doc.dateiname ?? "(kein Key)";
            summaryEl.innerText = doc.summary ?? "-";
        } catch (err) {
            titleEl.innerText = "Fehler";
            idEl.innerText = id;
            keyEl.innerText = "Das Dokument konnte nicht geladen werden.";
            summaryEl.innerText = "-";
            console.error(err);
        }
    }

    async function del() {
        if (!confirm("Willst du dieses Dokument wirklich löschen?")) return;
        try {
            const response = await apiFetch(`/api/dokumente/${id}`, { method: "DELETE" });
            if (response.ok) {
                alert("Dokument erfolgreich gelöscht.");
                window.location.href = "index.html";
            } else {
                const msg = await parseErrorResponse(response);
                alert("Fehler beim Löschen: " + msg);
            }
        } catch (error) {
            console.error("Fehler beim Löschen:", error);
            alert("Netzwerkfehler beim Löschen des Dokuments.");
        }
    }

    deleteButton?.addEventListener("click", del);
    await load();
}

// ---- Init ------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", async () => {
    protectPage();
    await loadNavbar();

    // page-specific wires
    const loginForm = document.getElementById("loginForm");
    loginForm?.addEventListener("submit", handleLogin);

    const registerForm = document.getElementById("registerForm");
    registerForm?.addEventListener("submit", handleRegister);

    wireUpload();
    await loadDocuments();
    await loadDetailPageIfPresent();
});
