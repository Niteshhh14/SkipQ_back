package com.skipq.backend.controller;

import com.skipq.backend.dto.StoreConnectRequest;
import com.skipq.backend.dto.StoreConnectResponse;
import com.skipq.backend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/connect")
    public ResponseEntity<StoreConnectResponse> connectStore(@RequestBody StoreConnectRequest request) {
        return ResponseEntity.ok(storeService.connectStore(request));
    }
}
