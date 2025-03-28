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

function toggleModifySection() {
    const editSection = document.getElementById("editSection1");
    const editButton = document.getElementById("editButton1");

    if (editSection.style.display === "none") {
        editSection.style.display = "block";
        editButton.textContent = "Cancel Edit";
    } else {
        editSection.style.display = "none";
        editButton.textContent = "Edit Reservation";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const statusText = document.getElementById("statusText").innerText.trim();
    const editButton = document.getElementById("editButton");

    if (statusText !== "CONFIRMED") {
        editButton.disabled = true;
    }
});

function toggleDeleteSection() {
    const deleteSection = document.getElementById("deleteSection");
    const deleteButton = document.getElementById("deleteButton");

    if (deleteSection.style.display === "none") {
        deleteSection.style.display = "block";
        deleteButton.textContent = "Cancel Edit";
    } else {
        deleteSection.style.display = "none";
        deleteButton.textContent = "Edit Reservation";
    }
}

function submitForm() {
    const form = document.getElementById("addToCalendarForm");

    fetch(form.action, {
        method: "POST",
        body: new FormData(form)
    }).then(response => {
        if (response.ok) {
            showPopup();
        }
    }).catch(error => {
        console.error("Error adding reservation to calendar:", error);
    });
}

function showPopup() {
    document.getElementById("popup").style.display = "block";
}

function closePopup() {
    document.getElementById("popup").style.display = "none";
}

function goToCalendar() {
    sessionStorage.setItem("fromReservationPage", "true");
    window.location.href = "/admin/calendar";
}