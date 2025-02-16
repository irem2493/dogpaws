package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.Product;
import com.dogpaws.backend.entity.rim.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {
    List<ProductOption> findByProduct(Product product);
    List<ProductOption> findByProductProductId(Integer productId);
}