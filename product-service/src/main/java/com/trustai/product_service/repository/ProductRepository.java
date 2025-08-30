package com.trustai.product_service.repository;

import com.trustai.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.category.name = :category AND p.isActive = true " +
            "AND p.price BETWEEN :min AND :max ORDER BY p.price DESC")
    List<Product> findTopByCategoryNameAndPriceBetweenAndIsActiveOrderByPriceDesc(
            @Param("category") String category,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max
    );
}
