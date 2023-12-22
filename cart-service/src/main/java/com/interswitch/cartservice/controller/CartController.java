package com.interswitch.cartservice.controller;

import com.interswitch.cartservice.dto.ErrorResponse;
import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.cartservice.exception.CartItemNotFoundException;
import com.interswitch.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/carts")
@Validated
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<ShoppingCartDTO> addItemToCart(@PathVariable Long userId, @Valid @RequestBody ShoppingCartDTO cartItem) {
        ShoppingCartDTO addedItem = cartService.addItemToCart(userId, cartItem);
        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ShoppingCartDTO>> getCartItems(@PathVariable Long userId) {
        List<ShoppingCartDTO> cartItems = cartService.getCartItems(userId);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }


    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFoundException(CartItemNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

