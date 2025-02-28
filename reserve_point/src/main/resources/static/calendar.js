document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek', // Weekly view
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
            alert('Reservation: ' + info.event.title);
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