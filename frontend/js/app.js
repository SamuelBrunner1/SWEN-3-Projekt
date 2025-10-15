// Dokumente vom Server laden und anzeigen
async function loadDocuments() {
    try {
        const response = await fetch("/api/dokumente");
        if (!response.ok) throw new Error("Fehler beim Laden der Dokumente");

        const data = await response.json();

        const container = document.getElementById("dokumente");
        container.innerHTML = "";

        if (data.length === 0) {
            container.innerHTML = `<div class="alert alert-info">Keine Dokumente vorhanden.</div>`;
        } else {
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
        }

    } catch (error) {
        console.error("Ladefehler:", error);
        document.getElementById("dokumente").innerText = "Fehler beim Laden der Dokumente.";
    }
}

// Dokument löschen
async function deleteDocument(id) {
    if (!confirm(`Dokument #${id} wirklich löschen?`)) return;

    try {
        const response = await fetch(`/api/dokumente/${id}`, { method: "DELETE" });
        if (response.ok) {
            alert(`Dokument #${id} gelöscht.`);
            loadDocuments(); // Liste neu laden
        } else {
            const msg = await response.text();
            alert(`Fehler beim Löschen: ${msg}`);
        }
    } catch (error) {
        console.error("Fehler beim Löschen:", error);
        alert("Netzwerkfehler beim Löschen des Dokuments.");
    }
}

// Neues Dokument per Formular hochladen
document.getElementById("uploadForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const titel = document.getElementById("titel").value;
    const inhalt = document.getElementById("inhalt").value;

    try {
        const response = await fetch("/api/dokumente", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ titel, inhalt })
        });

        const result = await response.text();  // Antwort zur Debug-Analyse
        console.log("Status:", response.status);
        console.log("Antwort:", result);

        if (response.ok) {
            document.getElementById("uploadMessage").innerText = "Dokument erfolgreich hochgeladen!";
            document.getElementById("uploadForm").reset();
            loadDocuments();
        } else {
            document.getElementById("uploadMessage").innerText = "Fehler beim Hochladen: " + result;
        }

    } catch (error) {
        console.error("Fehler beim POST:", error);
        document.getElementById("uploadMessage").innerText = "Netzwerkfehler beim Hochladen.";
    }
});

// Beim Laden der Seite Dokumente anzeigen
loadDocuments();
