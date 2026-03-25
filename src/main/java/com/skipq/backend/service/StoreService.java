package com.skipq.backend.service;

import com.skipq.backend.dto.StoreConnectRequest;
import com.skipq.backend.dto.StoreConnectResponse;
import com.skipq.backend.exception.ResourceNotFoundException;
import com.skipq.backend.model.Store;
import com.skipq.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreConnectResponse connectStore(StoreConnectRequest request) {
        Store store = storeRepository.findByStoreCode(request.getStoreCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with code: " + request.getStoreCode()));

        return StoreConnectResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .storeCode(store.getStoreCode())
                .location(store.getLocation())
                .build();
    }
}
