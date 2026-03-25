package com.skipq.backend.service;

import com.skipq.backend.dto.BillItemResponse;
import com.skipq.backend.dto.BillResponse;
import com.skipq.backend.dto.CheckoutRequest;
import com.skipq.backend.dto.CheckoutResponse;
import com.skipq.backend.exception.BadRequestException;
import com.skipq.backend.exception.ResourceNotFoundException;
import com.skipq.backend.model.*;
import com.skipq.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        // Find the most recent active cart for this user regardless of storeId
        List<Cart> carts = cartRepository.findByUserId(request.getUserId());
        if (carts.isEmpty()) {
            throw new ResourceNotFoundException("No active cart found for user: " + request.getUserId());
        }
        Cart cart = carts.get(carts.size() - 1);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty. Cannot checkout.");
        }

        // Calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Create order — simulate payment success; derive storeId from the cart itself
        Order order = Order.builder()
                .userId(request.getUserId())
                .storeId(cart.getStoreId())
                .totalAmount(total)
                .paymentStatus("PAID")
                .timestamp(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        // Create order items
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(product.getId())
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Clear cart after checkout
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .message("Payment successful. Your order has been placed.")
                .build();
    }

    public BillResponse getBill(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<BillItemResponse> billItems = orderItems.stream().map(item -> {
            Product p = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return BillItemResponse.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .imageUrl(p.getImageUrl())
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        return BillResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .storeId(order.getStoreId())
                .items(billItems)
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .timestamp(order.getTimestamp())
                .build();
    }
}
