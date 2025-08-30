package com.trustai.product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.trustai.common.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 255, unique = false, nullable = false, insertable = true, updatable = false)
    private String name;

    //#########################################
    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    private int qty = 0;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType = CurrencyType.BTC;
    //#########################################

    @Column(name = "product_code", length = 255, nullable = true)
    private String productCode; //StockKeepingUnit

    @Column(name = "sku", length = 180)
    private String sku; //StockKeepingUnit

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "display_url", length = 1000)
    private String displayUrl;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    // #########################################
    @Transient
    private boolean isPurchased;
    @Transient
    private TransactionStatus transactionStatus;
    // #########################################

    private LocalDateTime createdAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public Product addCategory(Category category) {
        this.category = category;
        return this;
    }

    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }




}
