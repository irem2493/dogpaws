package com.dogpaws.backend.entity.rim;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_detail_url")
    private String imageDetailUrl;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(length = 50)
    private String size;

    private String material;
    private String origin;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(length = 50)
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "main_category", length = 1)
    private String mainCategory;

    @Column(name = "sub_category", length = 1)
    private String subCategory;

    @Column(name = "storage_info")
    private String storageInfo;

    @Column(length = 50)
    private String weight;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 생성자는 Builder 패턴
    @Builder
    public Product(String name, Integer price, Integer stockQuantity,
                   String description, String status, String mainCategory,
                   String subCategory, String material, String origin,
                   LocalDate expirationDate, String color, String weight,
                   String size, String storageInfo) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.status = status;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.material = material;
        this.origin = origin;
        this.expirationDate = expirationDate;
        this.color = color;
        this.weight = weight;
        this.size = size;
        this.storageInfo = storageInfo;
    }

    public void update(String name, Integer price,
                       Integer stockQuantity, String description,
                       String status, String mainCategory,
                       String subCategory, String material,
                       String origin, LocalDate expirationDate,
                       String weight) {
        // 필수 필드 업데이트
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.status = status;
        this.mainCategory = mainCategory;

        // 선택 필드 업데이트
        this.subCategory = subCategory;
        this.material = material;
        this.origin = origin;
        this.expirationDate = expirationDate;
        this.weight = weight;
    }

    // 이미지 URL 업데이트 메서드
    public void updateImages(String imageUrl, String imageDetailUrl) {
        this.imageUrl = imageUrl;
        this.imageDetailUrl = imageDetailUrl;
    }
}