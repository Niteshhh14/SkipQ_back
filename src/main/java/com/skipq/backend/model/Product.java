package com.skipq.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;

    private String name;

    private BigDecimal price;

    @Column(unique = true)
    private String barcode;

    private String imageUrl;

    private Integer stockQuantity;
}
