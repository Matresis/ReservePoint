function toggleRejectSection(button) {
    const section = button.nextElementSibling;

    if (section.style.display === "none" || section.style.display === "") {
        section.style.display = "block";
        button.textContent = "Cancel Rejection";
    } else {
        section.style.display = "none";
        button.textContent = "Reject";
    }
}


function showSection(type) {
    const sections = ['confirmation', 'modification', 'cancellation'];
    sections.forEach(s => {
        document.getElementById('section-' + s).classList.add('hidden-section');
    });
    document.getElementById('section-' + type).classList.remove('hidden-section');
}

// Optional: show confirmation section by default on load
document.addEventListener('DOMContentLoaded', () => {
    showSection('confirmation');
});
