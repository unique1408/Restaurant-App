// Cart Management System
let cart = {
    items: [],
    totalPrice: 0,
    itemCount: 0
};

// Initialize cart on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeCart();
    setupCartEventListeners();
});

// Initialize cart from server or localStorage
function initializeCart() {
    // Try to load from server first
    fetchCartFromServer()
        .catch(() => {
            // Fallback to localStorage if server fails
            loadCartFromStorage();
        });
}

// Fetch cart from server
async function fetchCartFromServer() {
    try {
        const response = await fetch('/api/cart');
        if (response.ok) {
            const data = await response.json();
            cart = data;
            updateCartUI();
            saveCartToStorage();
        }
    } catch (error) {
        console.error('Error fetching cart:', error);
        throw error;
    }
}

// Load cart from localStorage
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('restaurantCart');
    if (savedCart) {
        cart = JSON.parse(savedCart);
        updateCartUI();
    }
}

// Save cart to localStorage
function saveCartToStorage() {
    localStorage.setItem('restaurantCart', JSON.stringify(cart));
}

// Setup cart event listeners
function setupCartEventListeners() {
    // Quantity change listeners
    document.querySelectorAll('.qty-input').forEach(input => {
        input.addEventListener('change', function() {
            const itemId = this.dataset.itemId;
            const newQuantity = parseInt(this.value);
            updateQuantity(itemId, newQuantity);
        });
    });

    // Remove item listeners
    document.querySelectorAll('.btn-remove').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const itemId = this.dataset.itemId;
            removeFromCart(itemId);
        });
    });

    // Clear cart listener
    const clearCartBtn = document.getElementById('clearCartBtn');
    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            clearCart();
        });
    }
}

// Add item to cart
function addToCart(dishId, quantity = 1) {
    const button = event?.target;
    if (button) {
        button.disabled = true;
        button.textContent = 'Adding...';
    }

    fetch('/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            dishId: dishId,
            quantity: quantity
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = data.cart;
            updateCartUI();
            saveCartToStorage();
            showNotification('Item added to cart!', 'success');
            
            // Animate cart icon
            animateCartIcon();
        } else {
            showNotification(data.message || 'Error adding item', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error adding item to cart', 'error');
    })
    .finally(() => {
        if (button) {
            button.disabled = false;
            button.textContent = 'Add to Cart';
        }
    });
}

// Update item quantity
function updateQuantity(itemId, newQuantity) {
    if (newQuantity < 1) {
        removeFromCart(itemId);
        return;
    }

    fetch(`/api/cart/update/${itemId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ quantity: newQuantity })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = data.cart;
            updateCartUI();
            saveCartToStorage();
            showNotification('Cart updated', 'success');
        }
    })
    .catch(error => {
        console.error('Error updating quantity:', error);
        showNotification('Error updating cart', 'error');
    });
}

// Remove item from cart
function removeFromCart(itemId) {
    if (!confirm('Remove this item from cart?')) {
        return;
    }

    fetch(`/api/cart/remove/${itemId}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = data.cart;
            updateCartUI();
            saveCartToStorage();
            showNotification('Item removed from cart', 'success');
            
            // Remove item element with animation
            removeItemWithAnimation(itemId);
        }
    })
    .catch(error => {
        console.error('Error removing item:', error);
        showNotification('Error removing item', 'error');
    });
}

// Clear entire cart
function clearCart() {
    if (!confirm('Are you sure you want to clear your cart?')) {
        return;
    }

    fetch('/api/cart/clear', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = {
                items: [],
                totalPrice: 0,
                itemCount: 0
            };
            updateCartUI();
            saveCartToStorage();
            showNotification('Cart cleared', 'success');
            
            // Animate items disappearing
            animateCartClear();
        }
    })
    .catch(error => {
        console.error('Error clearing cart:', error);
        showNotification('Error clearing cart', 'error');
    });
}

// Update entire cart UI
function updateCartUI() {
    updateCartCount();
    updateCartTotal();
    updateCartItems();
    updateCheckoutButton();
}

// Update cart count badge
function updateCartCount() {
    const countElements = document.querySelectorAll('.cart-count');
    countElements.forEach(el => {
        el.textContent = cart.itemCount || 0;
        
        // Add pulse animation if count changed
        el.classList.add('pulse');
        setTimeout(() => el.classList.remove('pulse'), 300);
    });
}

// Update cart total price
function updateCartTotal() {
    const totalElements = document.querySelectorAll('.cart-total, .total-price');
    totalElements.forEach(el => {
        el.textContent = `$${cart.totalPrice.toFixed(2)}`;
    });
}

// Update cart items list
function updateCartItems() {
    const cartContainer = document.getElementById('cartItems');
    if (!cartContainer) return;

    if (cart.items.length === 0) {
        cartContainer.innerHTML = `
            <div class="empty-cart">
                <div class="empty-cart-icon">🛒</div>
                <h3>Your cart is empty</h3>
                <p>Looks like you haven't added any items yet</p>
                <a href="/menu" class="btn-primary">Browse Menu</a>
            </div>
        `;
        return;
    }

    cartContainer.innerHTML = cart.items.map(item => `
        <div class="cart-item" id="cart-item-${item.id}">
            <div class="item-image">
                <img src="${item.dish.imagePath || '/images/dish1.jpg'}" alt="${item.dish.name}">
            </div>
            <div class="item-details">
                <h3>${item.dish.name}</h3>
                <p class="item-description">${item.dish.description}</p>
                <div class="item-price">$${item.dish.price.toFixed(2)}</div>
            </div>
            <div class="item-quantity">
                <button class="qty-btn minus" onclick="decrementQuantity(${item.id})">-</button>
                <input type="number" class="qty-input" data-item-id="${item.id}" 
                       value="${item.quantity}" min="1" max="10" readonly>
                <button class="qty-btn plus" onclick="incrementQuantity(${item.id})">+</button>
            </div>
            <div class="item-subtotal">
                $${item.subtotal.toFixed(2)}
            </div>
            <div class="item-actions">
                <button class="btn-remove" data-item-id="${item.id}" onclick="removeFromCart(${item.id})">
                    <span class="remove-icon">🗑️</span>
                </button>
            </div>
        </div>
    `).join('');
}

// Update checkout button
function updateCheckoutButton() {
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        if (cart.items.length === 0) {
            checkoutBtn.disabled = true;
            checkoutBtn.classList.add('disabled');
        } else {
            checkoutBtn.disabled = false;
            checkoutBtn.classList.remove('disabled');
        }
    }
}

// Quantity control functions
function incrementQuantity(itemId) {
    const item = cart.items.find(i => i.id === itemId);
    if (item && item.quantity < 10) {
        updateQuantity(itemId, item.quantity + 1);
    }
}

function decrementQuantity(itemId) {
    const item = cart.items.find(i => i.id === itemId);
    if (item) {
        if (item.quantity > 1) {
            updateQuantity(itemId, item.quantity - 1);
        } else {
            removeFromCart(itemId);
        }
    }
}

// Animation functions
function animateCartIcon() {
    const cartIcon = document.querySelector('.nav-cart a');
    if (cartIcon) {
        cartIcon.classList.add('cart-bounce');
        setTimeout(() => cartIcon.classList.remove('cart-bounce'), 500);
    }
}

function removeItemWithAnimation(itemId) {
    const itemElement = document.getElementById(`cart-item-${itemId}`);
    if (itemElement) {
        itemElement.classList.add('fade-out');
        setTimeout(() => {
            itemElement.remove();
            if (cart.items.length === 0) {
                updateCartItems(); // This will show empty cart message
            }
        }, 300);
    }
}

function animateCartClear() {
    const items = document.querySelectorAll('.cart-item');
    items.forEach((item, index) => {
        setTimeout(() => {
            item.classList.add('fade-out');
        }, index * 100);
    });
}

// Cart summary calculation
function calculateCartSummary() {
    const subtotal = cart.totalPrice;
    const deliveryFee = subtotal > 50 ? 0 : 5;
    const tax = subtotal * 0.1; // 10% tax
    const total = subtotal + deliveryFee + tax;

    return {
        subtotal: subtotal,
        deliveryFee: deliveryFee,
        tax: tax,
        total: total
    };
}

// Apply promo code
function applyPromoCode(code) {
    fetch('/api/cart/apply-promo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ code: code })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = data.cart;
            updateCartUI();
            saveCartToStorage();
            showNotification('Promo code applied!', 'success');
        } else {
            showNotification(data.message || 'Invalid promo code', 'error');
        }
    })
    .catch(error => {
        console.error('Error applying promo:', error);
        showNotification('Error applying promo code', 'error');
    });
}

// Save for later
function saveForLater(itemId) {
    fetch(`/api/cart/save-for-later/${itemId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cart = data.cart;
            updateCartUI();
            saveCartToStorage();
            showNotification('Item saved for later', 'success');
        }
    });
}

// Estimate delivery time
function estimateDelivery() {
    const now = new Date();
    const deliveryTime = new Date(now.getTime() + 45 * 60000); // Add 45 minutes
    
    const timeString = deliveryTime.toLocaleTimeString([], { 
        hour: '2-digit', 
        minute: '2-digit' 
    });
    
    const deliveryEstimate = document.getElementById('deliveryEstimate');
    if (deliveryEstimate) {
        deliveryEstimate.textContent = `Estimated delivery by ${timeString}`;
    }
}

// Add CSS for animations
const cartStyles = document.createElement('style');
cartStyles.textContent = `
    .cart-bounce {
        animation: bounce 0.5s ease;
    }
    
    @keyframes bounce {
        0%, 100% { transform: scale(1); }
        25% { transform: scale(1.2); }
        50% { transform: scale(0.9); }
        75% { transform: scale(1.1); }
    }
    
    .fade-out {
        opacity: 0;
        transform: translateX(100px);
        transition: all 0.3s ease;
    }
    
    .cart-item {
        transition: all 0.3s ease;
    }
    
    .qty-btn:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }
    
    .btn-checkout.disabled {
        opacity: 0.5;
        pointer-events: none;
    }
    
    .empty-cart {
        text-align: center;
        padding: 3rem;
    }
    
    .empty-cart-icon {
        font-size: 4rem;
        margin-bottom: 1rem;
    }
`;

document.head.appendChild(cartStyles);

// Export functions for global use
window.addToCart = addToCart;
window.removeFromCart = removeFromCart;
window.updateQuantity = updateQuantity;
window.clearCart = clearCart;
window.incrementQuantity = incrementQuantity;
window.decrementQuantity = decrementQuantity;
window.applyPromoCode = applyPromoCode;
window.saveForLater = saveForLater;

// Initialize delivery estimate
if (document.getElementById('deliveryEstimate')) {
    estimateDelivery();
    // Update every minute
    setInterval(estimateDelivery, 60000);
}