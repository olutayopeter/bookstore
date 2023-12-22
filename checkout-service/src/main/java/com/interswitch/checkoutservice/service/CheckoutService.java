package com.interswitch.checkoutservice.service;

import com.interswitch.checkoutservice.dto.CheckoutDTO;
import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;

import java.util.List;

public interface CheckoutService {

    String processCheckout(Long userId, PaymentOption paymentOption);

    List<CheckoutDTO> getPurchaseHistory(Long userId);
}
