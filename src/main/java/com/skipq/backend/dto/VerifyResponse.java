package com.skipq.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyResponse {
    private Long orderId;
    private boolean valid;
    private String message;
}
