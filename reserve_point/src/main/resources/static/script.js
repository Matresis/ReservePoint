function toggleFilterForm() {
    const form = document.getElementById("filter-form");
    form.style.display = form.style.display === "none" ? "block" : "none";
}

function toggleEditSection() {
    const editSection = document.getElementById("editSection");
    const editButton = document.getElementById("editButton");

    if (editSection.style.display === "none") {
        editSection.style.display = "block";
        editButton.textContent = "Cancel Edit";
    } else {
        editSection.style.display = "none";
        editButton.textContent = "Edit Reservation";
    }
}