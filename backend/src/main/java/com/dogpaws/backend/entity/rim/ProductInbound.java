package com.dogpaws.backend.entity.rim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_product_inbound")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inboundId;

    private Long optionId;
    private Integer quantity;
    private Integer costPrice;

    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}