package com.interswitch.checkoutservice.dto;

import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.entity.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutDTO {

    private Long userId;
    private List<Long> bookIds;
    private int quantity;
    private PaymentOption paymentOption;
    private PaymentStatus paymentStatus;


}
