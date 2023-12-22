package com.interswitch.cartservice.service;

import com.interswitch.cartservice.dto.ShoppingCartDTO;

import java.util.List;

public interface CartService {

    ShoppingCartDTO addItemToCart(Long userId, ShoppingCartDTO cartItem);

    List<ShoppingCartDTO> getCartItems(Long userId);

}
