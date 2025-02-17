package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.CartItem;
import com.dogpaws.backend.entity.rim.CartItemOption;
import com.dogpaws.backend.entity.rim.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {
    List<CartItemOption> findByCartItem(CartItem cartItem);
    Optional<CartItemOption> findByCartItemAndOption(CartItem cartItem, ProductOption option);
}