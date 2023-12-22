package com.interswitch.cartservice.repository;

import com.interswitch.cartservice.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<ShoppingCart, Long> {

    List<ShoppingCart> findByUserId(Long userId);
}
