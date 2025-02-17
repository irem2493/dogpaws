package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCartAndProduct(Cart cart, Product product);
}
