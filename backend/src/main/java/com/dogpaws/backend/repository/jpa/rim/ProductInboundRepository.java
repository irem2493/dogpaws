package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.ProductInbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInboundRepository extends JpaRepository<ProductInbound, Long> {
}