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

document.getElementById("saveChangesButton").addEventListener("click", function () {
    let reservationId = document.getElementById("reservationId").value;
    let status = document.getElementById("status").value;
    let orderedTime = document.getElementById("orderedTime").value;
    let notes = document.getElementById("notes").value;

    fetch("/admin/reservations/update", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `id=${reservationId}&status=${status}&orderedTime=${orderedTime}&notes=${encodeURIComponent(notes)}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.message) {
                alert(data.message);
                document.getElementById("statusText").innerText = data.status;
                document.getElementById("orderedTimeText").innerText = data.orderedTime;
                document.getElementById("notesText").innerText = data.notes;
                toggleEditSection();
            }
        })
        .catch(error => console.error("Error updating reservation:", error));
});

function deleteReservation() {
    let reservationId = document.getElementById("reservationId").value;

    if (!confirm("Are you sure you want to delete this reservation?")) return;

    fetch("/admin/reservations/delete", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `id=${reservationId}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.message) {
                alert(data.message);
                window.location.href = "/admin/reservations";
            }
        })
        .catch(error => console.error("Error deleting reservation:", error));
}

function submitForm() {
    const form = document.getElementById("addToCalendarForm");

    // Perform form submission using Fetch API (AJAX) to avoid page reload
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
    window.location.href = "/admin/reservations/calendar";
}