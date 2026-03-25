package com.skipq.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private Long storeId;
    private String name;
    private BigDecimal price;
    private String barcode;
    private String imageUrl;
    private Integer stockQuantity;
}
