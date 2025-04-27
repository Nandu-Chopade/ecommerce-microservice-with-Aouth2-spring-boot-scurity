package com.onlineshop.cart_service.services;

import com.onlineshop.cart_service.model.Cart;
import com.onlineshop.cart_service.model.CartItem;
import com.onlineshop.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok (Auto constructor injection)
public class CartService {
    private final CartRepository cartRepository;

    public Cart addToCart(Long userId, CartItem item) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(userId));
        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }
}