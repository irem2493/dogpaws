package com.dogpaws.backend.entity.rim;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_product_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_price")
    private Integer optionPrice;

    @Column(name = "option_stock")
    private Integer optionStock;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public ProductOption(Product product, String optionName,
                         Integer optionPrice, Integer optionStock) {
        this.product = product;
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.optionStock = optionStock;
    }

    // 재고 수정 메서드
    public void updateStock(Integer optionStock) {
        this.optionStock = optionStock;
    }

    // 옵션 정보 수정 메서드
    public void updateOption(String optionName, Integer optionPrice, Integer optionStock) {
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.optionStock = optionStock;
    }
}