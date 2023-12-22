package com.interswitch.checkoutservice.service.impl;

import com.interswitch.cartservice.dto.ShoppingCartDTO;
import com.interswitch.cartservice.exception.CartEmptyException;
import com.interswitch.checkoutservice.dto.CheckoutDTO;
import com.interswitch.checkoutservice.entity.Checkout;
import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.entity.enumeration.PaymentStatus;
import com.interswitch.checkoutservice.repository.CheckoutRepository;
import com.interswitch.checkoutservice.service.CheckoutService;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CheckoutServiceImpl(CheckoutRepository checkoutRepository, RestTemplate restTemplate) {
        this.checkoutRepository = checkoutRepository;
        this.restTemplate = restTemplate;
    }

    public String processCheckout(Long userId,PaymentOption paymentOption) {
        // Call User Microservice to get the user information
        UserResponse user = restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, userId);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        // Get the user's shopping cart items
        List<ShoppingCartDTO> cartItems = restTemplate.getForObject("http://cart-service/carts/{userId}", List.class, userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new CartEmptyException("User's shopping cart is empty");
        }

        // Perform the checkout logic
        // Calculate total amount, update inventory, etc.
        BigDecimal totalAmount = calculateTotalAmount(cartItems);

        // Simulate payment process (you can replace this with an actual payment integration)
        switch (paymentOption) {
            case WEB:
                // Implement logic for web payment
                break;
            case USSD:
                // Implement logic for USSD payment
                break;
            case TRANSFER:
                // Implement logic for bank transfer payment
                break;
            default:
                throw new IllegalArgumentException("Invalid payment option: " + paymentOption);
        }

        boolean paymentSuccessful = simulatePayment(totalAmount);

        // Update the purchase history based on the payment status
        PaymentStatus paymentStatus = paymentSuccessful ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;
        checkoutRepository.save(new Checkout(userId, extractBookIds(cartItems), cartItems.size(), paymentOption, paymentStatus));
        return paymentSuccessful ? "Checkout process completed successfully" : "Payment failed during checkout";
    }

    @Override
    public List<CheckoutDTO> getPurchaseHistory(Long userId) {
        // Call User Microservice to get the user information
        UserResponse user = restTemplate.getForObject("http://user-service/users/{userId}", UserResponse.class, userId);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        // Retrieve purchase history from the database based on userId
        List<Checkout> purchaseHistoryList = checkoutRepository.findByUserId(userId);

        // Convert entities to DTOs
        return purchaseHistoryList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(List<ShoppingCartDTO> cartItems) {
        return cartItems.stream()
                .map(ShoppingCartDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean simulatePayment(BigDecimal totalAmount) {
        // Simulate the payment process
        // You might want to integrate with a payment gateway in a real-world scenario
        // For simplicity, we'll assume the payment is always successful in this example
        return true;
    }

    private List<Long> extractBookIds(List<ShoppingCartDTO> cartItems) {
        // Extract book IDs from the shopping cart items
        return cartItems.stream()
                .map(ShoppingCartDTO::getBookId)
                .collect(Collectors.toList());
    }

    private CheckoutDTO convertToDTO(Checkout purchaseHistory) {
        return new CheckoutDTO(
                purchaseHistory.getUserId(),
                purchaseHistory.getBookIds(),
                purchaseHistory.getQuantity(),
                purchaseHistory.getPaymentOption(),
                purchaseHistory.getPaymentStatus()
        );
    }

}
