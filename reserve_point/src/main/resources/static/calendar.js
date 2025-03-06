document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek', // Weekly view
        height: 'auto', //
        weekends: false, // Disable weekends
        locale: 'en', // Set language
        headerToolbar: {
            left: 'prev,next',
            center: 'title',
            right: 'timeGridWeek,timeGridDay'
        },
        events: '/admin/reservations/calendar/events', // Fetch events from backend
        slotMinTime: "08:00:00", // Workday start time
        slotMaxTime: "18:00:00",  // Workday end time
        nowIndicator: true, // Show current time indicator
        eventClick: function (info) {
            // Fetch reservation details
            fetch(`/admin/reservations/calendar/${info.event.id}`)
                .then(response => response.json())
                .then(data => {
                    showReservationPopup(data);
                })
                .catch(error => console.error('Error loading reservation:', error));
        },
        datesSet: function() {
            setTimeout(() => {
                let timeGridSlots = document.querySelector(".fc-timegrid-slots");
                let timeGridCols = document.querySelector(".fc-timegrid-cols");

                if (timeGridSlots && timeGridCols) {
                    let height = timeGridSlots.offsetHeight;
                    timeGridCols.style.height = height + "px";
                }
            }, 500);
        }
    });
    calendar.render();
});

function toggleEditSection() {
    const editSection = document.getElementById("popup-editSection");
    const editButton = document.getElementById("editButton");

    if (editSection.style.display === "none") {
        editSection.style.display = "block";
        editButton.textContent = "Cancel Edit";
    } else {
        editSection.style.display = "none";
        editButton.textContent = "Edit Reservation";
    }
}

function confirmDelete() {
    const deleteOptions = document.getElementById("deleteOptions");
    const deleteButton = document.getElementById("deleteButton");

    if (deleteOptions.style.display === "none") {
        deleteOptions.style.display = "block";
        deleteButton.textContent = "Cancel";
    } else {
        deleteOptions.style.display = "none";
        deleteButton.textContent = "Delete Reservation";
    }
}

// Function to populate and show the pop-up
function showReservationPopup(reservation) {
    document.getElementById("popup-name").textContent = reservation.customer.user.name;
    document.getElementById("popup-surname").textContent = reservation.customer.user.surname;
    document.getElementById("popup-email").textContent = reservation.customer.user.email;
    document.getElementById("popup-phone").textContent = reservation.customer.phone;
    document.getElementById("popup-service").textContent = reservation.service.name;
    document.getElementById("popup-created").textContent = reservation.formattedCreatedAt;
    document.getElementById("popup-status").textContent = reservation.status;
    document.getElementById("popup-date").textContent = reservation.formattedOrderTime;
    document.getElementById("popup-notes").textContent = reservation.notes;

    // Populate edit form fields
    document.getElementById("edit-id").value = reservation.id;
    document.getElementById("edit-orderedTime").value = reservation.formattedOrderTime;
    document.getElementById("edit-status").value = reservation.status;
    document.getElementById("edit-notes").value = reservation.notes || "";

    // Populate delete forms
    document.getElementById("delete-id").value = reservation.id;

    document.getElementById("popup").style.display = "flex";
}

// Function to close the pop-up
function closePopup() {
    document.getElementById("popup").style.display = "none";
}

document.addEventListener("DOMContentLoaded", function () {
    const backButton = document.getElementById("return-button");

    // Check if user came from reservation details page
    if (sessionStorage.getItem("fromReservationPage") === "true") {
        backButton.href = "/admin/reservations"; // Change back button link
        backButton.content = "Back to Reservations";
        sessionStorage.removeItem("fromReservationPage"); // Clear the flag
    } else {
        backButton.href = "/admin";
    }
});
