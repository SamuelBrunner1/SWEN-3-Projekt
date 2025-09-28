// Dokumente vom Server laden und anzeigen
async function loadDocuments() {
    try {
        const response = await fetch("/api/dokumente");
        if (!response.ok) throw new Error("Fehler beim Laden der Dokumente");

        const data = await response.json();

        const container = document.getElementById("dokumente");
        container.innerHTML = "";

        if (data.length === 0) {
            container.innerText = "Keine Dokumente vorhanden.";
        } else {
            data.forEach(doc => {
                const div = document.createElement("div");
                div.innerHTML = `<a href="detail.html?id=${doc.id}">${doc.titel}</a>`;
                container.appendChild(div);
            });
        }
    } catch (error) {
        console.error("Ladefehler:", error);
        document.getElementById("dokumente").innerText = "Fehler beim Laden der Dokumente.";
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
