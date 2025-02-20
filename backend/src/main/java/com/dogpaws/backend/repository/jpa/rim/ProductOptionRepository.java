package com.dogpaws.backend.repository.jpa.rim;

import com.dogpaws.backend.entity.rim.Product;
import com.dogpaws.backend.entity.rim.ProductOption;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct(Product product);
    // 또는 Product 엔티티와의 관계를 통해 조회
    @Query("SELECT po FROM ProductOption po WHERE po.product.productId = :productId")
    List<ProductOption> findByProductId(@Param("productId") Long productId);
}