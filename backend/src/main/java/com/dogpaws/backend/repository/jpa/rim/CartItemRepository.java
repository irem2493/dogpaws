package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.Cart;
import com.dogpaws.backend.entity.rim.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}
