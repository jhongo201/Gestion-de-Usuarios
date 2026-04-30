/* ── Toggle sidebar en móvil ─────────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', function () {
    const toggle  = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');

    if (toggle && sidebar) {
        toggle.addEventListener('click', function () {
            sidebar.classList.toggle('show');
        });
    }

    /* Cerrar sidebar al hacer clic fuera en móvil */
    document.addEventListener('click', function (e) {
        if (sidebar && !sidebar.contains(e.target) &&
            toggle  && !toggle.contains(e.target)) {
            sidebar.classList.remove('show');
        }
    });
});