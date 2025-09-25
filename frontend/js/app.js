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
