// 🔹 Hilfsfunktion: JSON-Fehler oder Text extrahieren
async function parseErrorResponse(response) {
    try {
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            const err = await response.json();
            return err.error || err.message || JSON.stringify(err);
        } else {
            return await response.text();
        }
    } catch {
        return "Unbekannter Fehler beim Verarbeiten der Antwort.";
    }
}

// 🔹 Dokumente vom Server laden und anzeigen
async function loadDocuments() {
    const container = document.getElementById("dokumente");
    container.innerHTML = `<div class="alert alert-secondary">Lade Dokumente...</div>`;

    try {
        const response = await fetch("/api/dokumente");
        if (!response.ok) {
            const msg = await parseErrorResponse(response);
            throw new Error(msg);
        }

        const data = await response.json();
        container.innerHTML = "";

        if (!data || data.length === 0) {
            container.innerHTML = `<div class="alert alert-info">Keine Dokumente vorhanden.</div>`;
            return;
        }

        data.forEach((doc) => {
            const div = document.createElement("div");
            div.className = "p-3 border rounded mb-2 bg-white shadow-sm";
            div.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="mb-1">
                            <a href="detail.html?id=${doc.id}" class="text-decoration-none text-dark fw-bold">
                                ${doc.titel}
                            </a>
                        </h5>
                        <small class="text-muted">ID: ${doc.id}</small>
                    </div>
                    <div class="btn-group">
                        <a href="detail.html?id=${doc.id}" class="btn btn-sm btn-outline-primary">Öffnen</a>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteDocument(${doc.id})">
                            Löschen
                        </button>
                    </div>
                </div>
            `;
            container.appendChild(div);
        });

    } catch (error) {
        console.error("Ladefehler:", error);
        container.innerHTML = `<div class="alert alert-danger">Fehler beim Laden der Dokumente: ${error.message}</div>`;
    }
}

// 🔹 Dokument löschen
async function deleteDocument(id) {
    if (!confirm(`Dokument #${id} wirklich löschen?`)) return;

    try {
        const response = await fetch(`/api/dokumente/${id}`, { method: "DELETE" });

        if (response.ok) {
            alert(`Dokument #${id} gelöscht.`);
            loadDocuments(); // Liste neu laden
        } else {
            const msg = await parseErrorResponse(response);
            alert(`Fehler beim Löschen: ${msg}`);
        }
    } catch (error) {
        console.error("Fehler beim Löschen:", error);
        alert("Netzwerkfehler beim Löschen des Dokuments.");
    }
}

// 🔹 Neues Dokument per Formular hochladen
document.getElementById("uploadForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const titel = document.getElementById("titel").value.trim();
    const inhalt = document.getElementById("inhalt").value.trim();
    const messageBox = document.getElementById("uploadMessage");

    if (!titel || !inhalt) {
        messageBox.className = "text-danger";
        messageBox.innerText = "Bitte alle Felder ausfüllen.";
        return;
    }

    try {
        const response = await fetch("/api/dokumente", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ titel, inhalt })
        });

        if (response.ok) {
            messageBox.className = "text-success";
            messageBox.innerText = "Dokument erfolgreich hochgeladen!";
            document.getElementById("uploadForm").reset();
            loadDocuments();
        } else {
            const msg = await parseErrorResponse(response);
            messageBox.className = "text-danger";
            messageBox.innerText = "Fehler beim Hochladen: " + msg;
        }

    } catch (error) {
        console.error("Fehler beim POST:", error);
        messageBox.className = "text-danger";
        messageBox.innerText = "Netzwerkfehler beim Hochladen.";
    }
});

// Beim Laden der Seite Dokumente anzeigen
loadDocuments();
