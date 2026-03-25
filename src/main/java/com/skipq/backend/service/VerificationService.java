package com.skipq.backend.service;

import com.skipq.backend.dto.VerifyResponse;
import com.skipq.backend.exception.ResourceNotFoundException;
import com.skipq.backend.model.Order;
import com.skipq.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final OrderRepository orderRepository;

    public VerifyResponse verify(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isPaid = "PAID".equalsIgnoreCase(order.getPaymentStatus());

        return VerifyResponse.builder()
                .orderId(orderId)
                .valid(isPaid)
                .message(isPaid ? "Purchase verified. Customer may exit." : "Payment not completed.")
                .build();
    }
}
