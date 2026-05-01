// Global Variables
let cartCount = 0;

// Document Ready
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    loadCartCount();
    setupEventListeners();
});

// Initialize Application
function initializeApp() {
    console.log('RestaurantApp initialized');
    highlightCurrentPage();
    setupMobileMenu();
}

// Highlight Current Page in Navigation
function highlightCurrentPage() {
    const currentPage = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-menu a');
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });
}

// Mobile Menu Toggle
function setupMobileMenu() {
    const mobileMenuBtn = document.createElement('button');
    mobileMenuBtn.className = 'mobile-menu-btn';
    mobileMenuBtn.innerHTML = '☰';
    mobileMenuBtn.onclick = toggleMobileMenu;
    
    const nav = document.querySelector('.nav-container');
    if (nav && !document.querySelector('.mobile-menu-btn')) {
        nav.insertBefore(mobileMenuBtn, nav.querySelector('.nav-menu'));
    }
}

function toggleMobileMenu() {
    const navMenu = document.querySelector('.nav-menu');
    navMenu.classList.toggle('show');
}

// Cart Functions
function loadCartCount() {
    fetch('/api/cart/count')
        .then(response => response.json())
        .then(data => {
            cartCount = data.count;
            updateCartCount();
        })
        .catch(error => console.error('Error loading cart count:', error));
}

function updateCartCount() {
    const cartCountElements = document.querySelectorAll('.cart-count');
    cartCountElements.forEach(element => {
        element.textContent = cartCount;
        
        // Add animation
        element.classList.add('pulse');
        setTimeout(() => {
            element.classList.remove('pulse');
        }, 300);
    });
}

function addToCart(dishId, quantity = 1) {
    fetch(`/api/cart/add/${dishId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ quantity: quantity })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cartCount += quantity;
            updateCartCount();
            showNotification('Item added to cart!', 'success');
        } else {
            showNotification('Error adding item to cart', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error adding item to cart', 'error');
    });
}

function removeFromCart(itemId) {
    if (confirm('Remove this item from cart?')) {
        fetch(`/api/cart/remove/${itemId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                showNotification('Error removing item', 'error');
            }
        });
    }
}

function updateCartItemQuantity(itemId, quantity) {
    if (quantity < 1) return;
    
    fetch(`/api/cart/update/${itemId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ quantity: quantity })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        }
    });
}

// Notification System
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Form Validation
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePhone(phone) {
    const re = /^\d{10}$/;
    return re.test(phone);
}

function validatePassword(password) {
    // At least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
    const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return re.test(password);
}

// Form Submit Handler
function handleFormSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    
    fetch(form.action, {
        method: form.method,
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Form submitted successfully!', 'success');
            if (form.dataset.redirect) {
                setTimeout(() => {
                    window.location.href = form.dataset.redirect;
                }, 1500);
            }
        } else {
            showNotification(data.message || 'Error submitting form', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error submitting form', 'error');
    });
}

// Search Functionality
function performSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchTerm = searchInput.value.toLowerCase();
    const items = document.querySelectorAll('.searchable-item');
    
    items.forEach(item => {
        const text = item.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// Filter Functionality
function filterByCategory(category) {
    const items = document.querySelectorAll('.filterable-item');
    
    items.forEach(item => {
        if (category === 'all' || item.dataset.category === category) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// Sort Functionality
function sortItems(sortBy) {
    const container = document.querySelector('.sortable-container');
    const items = Array.from(document.querySelectorAll('.sortable-item'));
    
    items.sort((a, b) => {
        const aVal = a.dataset[sortBy];
        const bVal = b.dataset[sortBy];
        
        if (sortBy === 'price') {
            return parseFloat(aVal) - parseFloat(bVal);
        } else if (sortBy === 'name') {
            return aVal.localeCompare(bVal);
        } else if (sortBy === 'rating') {
            return parseFloat(bVal) - parseFloat(aVal);
        }
    });
    
    items.forEach(item => container.appendChild(item));
}

// Image Preview
function previewImage(input, previewId) {
    const preview = document.getElementById(previewId);
    
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
        
        reader.readAsDataURL(input.files[0]);
    }
}

// Load More Content (Pagination)
let currentPage = 1;
function loadMore() {
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    loadMoreBtn.disabled = true;
    loadMoreBtn.textContent = 'Loading...';
    
    fetch(`/api/load-more?page=${currentPage + 1}`)
        .then(response => response.json())
        .then(data => {
            if (data.items && data.items.length > 0) {
                appendItems(data.items);
                currentPage++;
                loadMoreBtn.disabled = false;
                loadMoreBtn.textContent = 'Load More';
            } else {
                loadMoreBtn.textContent = 'No More Items';
                loadMoreBtn.disabled = true;
            }
        });
}

function appendItems(items) {
    const container = document.querySelector('.items-container');
    items.forEach(item => {
        const element = createItemElement(item);
        container.appendChild(element);
    });
}

// Scroll to Top
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Back to Top Button
window.addEventListener('scroll', function() {
    const backToTopBtn = document.getElementById('backToTop');
    if (!backToTopBtn) {
        createBackToTopButton();
    }
    
    if (window.scrollY > 300) {
        document.getElementById('backToTop').style.display = 'block';
    } else {
        document.getElementById('backToTop').style.display = 'none';
    }
});

function createBackToTopButton() {
    const btn = document.createElement('button');
    btn.id = 'backToTop';
    btn.innerHTML = '↑';
    btn.onclick = scrollToTop;
    btn.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        width: 40px;
        height: 40px;
        background: #ff6b6b;
        color: white;
        border: none;
        border-radius: 50%;
        cursor: pointer;
        display: none;
        font-size: 20px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.3);
        z-index: 1000;
    `;
    
    document.body.appendChild(btn);
}

// Add CSS for notifications
const style = document.createElement('style');
style.textContent = `
    .notification {
        position: fixed;
        top: 20px;
        right: 20px;
        min-width: 300px;
        padding: 1rem;
        border-radius: 5px;
        box-shadow: 0 3px 10px rgba(0,0,0,0.2);
        z-index: 1000;
        animation: slideIn 0.3s ease;
    }
    
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    .notification-success {
        background: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
    }
    
    .notification-error {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
    
    .notification-info {
        background: #d1ecf1;
        color: #0c5460;
        border: 1px solid #bee5eb;
    }
    
    .notification-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .notification-close {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: inherit;
    }
    
    .pulse {
        animation: pulse 0.3s ease;
    }
    
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.2); }
        100% { transform: scale(1); }
    }
    
    .mobile-menu-btn {
        display: none;
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #333;
    }
    
    @media (max-width: 768px) {
        .mobile-menu-btn {
            display: block;
        }
        
        .nav-menu {
            display: none;
            position: absolute;
            top: 100%;
            left: 0;
            width: 100%;
            background: white;
            flex-direction: column;
            padding: 1rem;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .nav-menu.show {
            display: flex;
        }
        
        .nav-menu li {
            margin: 0.5rem 0;
        }
    }
`;

document.head.appendChild(style);