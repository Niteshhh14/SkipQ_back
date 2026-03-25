package com.skipq.backend.dto;

import lombok.Data;

@Data
public class ChatAssistRequest {
    private String userId;
    private String message;
    private Long storeId;
    private Boolean storeConnected;
    private Integer cartItemsCount;
    private Long lastOrderId;
}
