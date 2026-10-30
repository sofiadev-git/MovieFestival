document.addEventListener('DOMContentLoaded', () => {
  const toggle = document.querySelector('[data-toggle-menu]');
  const sidebar = document.getElementById('siteSidebar');
  toggle?.addEventListener('click', () => {
    const opened = sidebar?.classList.toggle('open') || false;
    toggle.setAttribute('aria-expanded', String(opened));
  });
  const normalize = value => (value || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('it-IT');
  document.querySelectorAll('[data-filter-input], [data-admin-search]').forEach(search => {
    const admin = search.hasAttribute('data-admin-search');
    const target = search.dataset.filterTarget || (admin ? '[data-admin-row]' : '[data-search]');
    const items = Array.from(document.querySelectorAll(target));
    const empty = document.querySelector(admin ? '[data-admin-empty]' : '[data-filter-empty]');
    const count = document.querySelector('[data-admin-count]');
    const update = () => {
      const term = normalize(search.value.trim());
      let visible = 0;
      items.forEach(item => {
        const match = normalize(item.dataset.search || item.textContent).includes(term);
        item.classList.toggle('filter-hidden', !match);
        item.hidden = admin && !match;
        if (match) visible++;
      });
      if (empty) { empty.hidden = admin ? visible !== 0 || items.length === 0 : false; empty.classList.toggle('visible', !admin && visible === 0 && items.length !== 0); }
      if (count) count.textContent = `${visible} ${visible === 1 ? 'risultato' : 'risultati'}`;
    };
    search.addEventListener('input', update);
    const q = new URLSearchParams(location.search).get('q');
    if (!admin && q && search.closest('main') && location.pathname.endsWith('/films')) search.value = q;
    update();
  });
  document.querySelectorAll('form[data-confirm], form[data-admin-confirm]').forEach(form => {
    form.addEventListener('submit', event => {
      const msg = form.dataset.confirm || form.dataset.adminConfirm || 'Confermi?';
      if (!window.confirm(msg)) event.preventDefault();
    });
  });
});
