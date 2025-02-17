package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMainCategory(String mainCategory);
}