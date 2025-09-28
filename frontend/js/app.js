async function loadDocuments() {
    const response = await fetch("/api/dokumente");
    const data = await response.json();

    const container = document.getElementById("dokumente");
    container.innerHTML = "";

    data.forEach(doc => {
        const div = document.createElement("div");
        div.innerHTML = `<a href="detail.html?id=${doc.id}">${doc.titel}</a>`;
        container.appendChild(div);
    });
}

loadDocuments();

document.getElementById("uploadForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const titel = document.getElementById("titel").value;
    const inhalt = document.getElementById("inhalt").value;

    const response = await fetch("/api/dokumente", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ titel, inhalt })
    });

    if (response.ok) {
        document.getElementById("uploadMessage").innerText = "Dokument erfolgreich hochgeladen!";
        document.getElementById("uploadForm").reset();
        loadDocuments(); // Liste neu laden
    } else {
        document.getElementById("uploadMessage").innerText = "Fehler beim Hochladen!";
    }
});
