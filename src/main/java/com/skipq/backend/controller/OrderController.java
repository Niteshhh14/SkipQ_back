package com.skipq.backend.controller;

import com.skipq.backend.dto.BillResponse;
import com.skipq.backend.dto.CheckoutRequest;
import com.skipq.backend.dto.CheckoutResponse;
import com.skipq.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(request));
    }

    @GetMapping({"/api/order/{orderId}", "/api/bill/{orderId}"})
    public ResponseEntity<BillResponse> getBill(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getBill(orderId));
    }
}
