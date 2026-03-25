package com.skipq.backend.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private String userId;
    private Long productId;
    private Integer quantity;
    private Long storeId;
}
