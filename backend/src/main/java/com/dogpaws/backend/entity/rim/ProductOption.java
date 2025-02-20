package com.dogpaws.backend.entity.rim;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Column(name = "option_size")
    private String optionSize;

    @Column(name = "option_color") 
    private String optionColor;

    @Column(name = "option_weight")
    private String optionWeight;

    @Column(name = "option_material")
    private String optionMaterial;

    @Column(name = "option_expiration_date")
    private LocalDate optionExpirationDate;

    @Column(name = "option_storage_info")
    private String optionStorageInfo;

    @Column(name = "option_manufacturer")
    private String optionManufacturer;

    @Column(name = "option_origin")
    private String optionOrigin;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isBaseOption = false;  // 기본 옵션 여부

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
                         Integer optionPrice, Integer optionStock,
                         String optionSize,
                         String optionColor,
                         String optionWeight,
                         String optionMaterial,
                         LocalDate optionExpirationDate,
                         String optionStorageInfo,
                         String optionManufacturer,
                         String optionOrigin, boolean isBaseOption) {
        this.product = product;
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.optionStock = optionStock;
        this.optionSize = optionSize;
        this.optionColor = optionColor;
        this.optionWeight = optionWeight;
        this.optionMaterial = optionMaterial;
        this.optionExpirationDate = optionExpirationDate;
        this.optionStorageInfo = optionStorageInfo;
        this.optionManufacturer = optionManufacturer;
        this.optionOrigin = optionOrigin;
        this.isBaseOption = isBaseOption;
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