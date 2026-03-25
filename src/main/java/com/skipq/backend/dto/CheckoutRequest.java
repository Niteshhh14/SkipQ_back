package com.skipq.backend.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String userId;
    private Long storeId;
}
