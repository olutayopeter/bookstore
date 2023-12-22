package com.interswitch.checkoutservice.controller;

import com.interswitch.checkoutservice.dto.CheckoutDTO;
import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> processCheckout(@PathVariable Long userId,@RequestParam PaymentOption paymentOption) {
        try {
            String result = checkoutService.processCheckout(userId, paymentOption);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return new ResponseEntity<>("Checkout failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/purchase-history/{userId}")
    public ResponseEntity<List<CheckoutDTO>> getPurchaseHistory(@PathVariable Long userId) {
        try {
            List<CheckoutDTO> purchaseHistory = checkoutService.getPurchaseHistory(userId);
            return new ResponseEntity<>(purchaseHistory, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return appropriate response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
