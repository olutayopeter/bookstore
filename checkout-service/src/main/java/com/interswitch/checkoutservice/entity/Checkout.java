package com.interswitch.checkoutservice.entity;

import com.interswitch.checkoutservice.entity.enumeration.PaymentOption;
import com.interswitch.checkoutservice.entity.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    private List<Long> bookIds;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public Checkout(Long userId, List<Long> bookIds, int quantity, PaymentOption paymentOption, PaymentStatus paymentStatus) {
        this.userId = userId;
        this.bookIds = bookIds;
        this.quantity = quantity;
        this.paymentOption = paymentOption;
        this.paymentStatus = paymentStatus;
    }


}
