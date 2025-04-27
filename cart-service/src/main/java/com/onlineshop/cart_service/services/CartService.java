package com.onlineshop.cart_service.services;

import com.onlineshop.cart_service.client.ProductClient;
import com.onlineshop.cart_service.dto.Product;
import com.onlineshop.cart_service.model.Cart;
import com.onlineshop.cart_service.model.CartItem;
import com.onlineshop.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok (Auto constructor injection)
public class CartService {

    private final CartRepository cartRepository;
    @Autowired
    private ProductClient productClient;


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


    public void addProductToCart(Long userId, Long productId, int quantity) {
        Product product = productClient.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()-> new UsernameNotFoundException("user not found!"));
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }

        cart.addItem(productId, product.getName(), quantity, product.getPrice());
        cartRepository.save(cart);
    }
}