package com.trustai.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_collections")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PURCHASED;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UserCollection(Long userId, Long productId, TransactionStatus transactionStatus) {
        this.userId = userId;
        this.productId = productId;
        this.status = transactionStatus;
    }
}
