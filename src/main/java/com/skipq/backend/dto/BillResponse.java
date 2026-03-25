package com.skipq.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponse {
    private Long orderId;
    private String userId;
    private Long storeId;
    private List<BillItemResponse> items;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime timestamp;
}
