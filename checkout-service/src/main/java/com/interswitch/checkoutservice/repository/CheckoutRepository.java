package com.interswitch.checkoutservice.repository;

import com.interswitch.checkoutservice.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    List<Checkout> findByUserId(Long userId);
}
