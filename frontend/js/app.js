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

// ---- Dokumente laden -------------------------------------------------------
async function loadDocuments() {
    const container = document.getElementById("dokumente");
    container.innerHTML = `<div class="alert alert-secondary">Lade Dokumente...</div>`;

    try {
        const response = await fetch("/api/dokumente");
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
            <small class="text-muted">Key: ${escapeHtml(doc.dateiname ?? "(kein Key)")}</small>
          </div>
          <div class="btn-group">
            <a href="detail.html?id=${doc.id}" class="btn btn-sm btn-outline-primary">Öffnen</a>
            <button class="btn btn-sm btn-outline-danger" onclick="deleteDocument(${doc.id})">Löschen</button>
          </div>
        </div>
      </div>
    `).join("");

    } catch (error) {
        console.error("Ladefehler:", error);
        container.innerHTML = `<div class="alert alert-danger">Fehler beim Laden der Dokumente: ${escapeHtml(error.message)}</div>`;
    }
}

// ---- Dokument löschen ------------------------------------------------------
async function deleteDocument(id) {
    if (!confirm(`Dokument #${id} wirklich löschen?`)) return;

    try {
        const res = await fetch(`/api/dokumente/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error(await parseErrorResponse(res));

        alert(`Dokument #${id} gelöscht.`);
        loadDocuments();
    } catch (error) {
        console.error("Fehler beim Löschen:", error);
        alert("Fehler beim Löschen: " + error.message);
    }
}

// ---- Datei-Upload (multipart -> /api/upload) --------------------------------
document.getElementById("uploadForm").addEventListener("submit", async (e) => {
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
        const res = await fetch("/api/upload", { method: "POST", body: fd });
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

// ---- Init ------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", loadDocuments);
