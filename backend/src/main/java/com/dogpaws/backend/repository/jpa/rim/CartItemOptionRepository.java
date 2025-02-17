package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.CartItem;
import com.dogpaws.backend.entity.rim.CartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {
    List<CartItemOption> findByCartItem(CartItem cartItem);
}