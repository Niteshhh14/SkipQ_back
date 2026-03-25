package com.skipq.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private String userId;
    private Long storeId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
}
