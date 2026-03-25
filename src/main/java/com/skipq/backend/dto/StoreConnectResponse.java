package com.skipq.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreConnectResponse {
    private Long id;
    private String storeName;
    private String storeCode;
    private String location;
}
