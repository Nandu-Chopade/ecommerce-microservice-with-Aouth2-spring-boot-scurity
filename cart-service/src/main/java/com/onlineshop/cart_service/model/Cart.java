package com.onlineshop.cart_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// Cart.java
@Entity
@Data // Lombok (Auto Getters/Setters)
@RequiredArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();


    public Cart(Long userId) {
        this.userId = userId;
    }

    public void addItem(Long productId, String productName, int quantity, double price) {
        items.add(new CartItem(productId, productName, quantity, price));
    }
}

