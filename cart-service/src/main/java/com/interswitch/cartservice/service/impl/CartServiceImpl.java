package com.interswitch.cartservice.service.impl;

import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.cartservice.entity.ShoppingCart;
import com.interswitch.cartservice.repository.CartRepository;
import com.interswitch.cartservice.service.CartService;
import com.interswitch.inventoryservice.dto.BookDTO;
import com.interswitch.inventoryservice.exception.BookNotFoundException;
import com.interswitch.inventoryservice.exception.InsufficientStockException;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, RestTemplate restTemplate) {
        this.cartRepository = cartRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public ShoppingCartDTO addItemToCart(Long userId, ShoppingCartDTO cartItem) {
        // Call User Microservice to check if the user exists
        UserResponse user = restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, userId);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        // Call Book Inventory Microservice to get book information
        BookDTO book = restTemplate.getForObject("http://book-service/books/{bookId}", BookDTO.class, cartItem.getBookId());

        if (book == null) {
            throw new BookNotFoundException("Book not found with id: " + cartItem.getBookId());
        }

        // Check if the book is available in inventory
        if (book.getAvailableQuantity() < cartItem.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for book with id: " + cartItem.getBookId());
        }

        // Save the item to the database
        ShoppingCart savedCartItem = cartRepository.save(new ShoppingCart(userId, cartItem.getBookId(), cartItem.getQuantity()));

        // Update book inventory (reduce available quantity)
        book.setAvailableQuantity(book.getAvailableQuantity() - cartItem.getQuantity());
        restTemplate.put("http://book-service/books/{bookId}", book, cartItem.getBookId());

        return convertToDTO(savedCartItem);
    }


    @Override
    public List<ShoppingCartDTO> getCartItems(Long userId) {
        // Retrieve cart items for a user from the database
        List<ShoppingCart> cartItems = cartRepository.findByUserId(userId);

        // Convert entities to DTOs
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ShoppingCartDTO convertToDTO(ShoppingCart shoppingCart) {
        return new ShoppingCartDTO(shoppingCart.getUserId(), shoppingCart.getBookId(), shoppingCart.getQuantity(),shoppingCart.getPrice());
    }

}
