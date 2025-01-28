document.querySelectorAll('.product-card').forEach(card => {
    card.addEventListener('click', (event) => {
        // Prevent clicks from bubbling up
        if (event.target.tagName !== 'INPUT' && event.target.tagName !== 'LABEL') {
            const checkbox = card.querySelector('.product-checkbox');
            checkbox.checked = !checkbox.checked; // Toggle the checkbox state
            card.classList.toggle('selected', checkbox.checked); // Add/remove selected class
        }
        event.stopPropagation(); // Prevent bubbling up
    });
});

// Optional: Show selected products count
const updateSelectedCount = () => {
    const selectedCount = document.querySelectorAll('.product-checkbox:checked').length;
    const selectedCountElement = document.getElementById('selected-count');
    if (selectedCountElement) {
        selectedCountElement.textContent = `${selectedCount} products selected`;
    }
};

document.querySelectorAll('.product-checkbox').forEach(checkbox => {
    checkbox.addEventListener('change', updateSelectedCount);
});

// Initial call to set the count
updateSelectedCount();
