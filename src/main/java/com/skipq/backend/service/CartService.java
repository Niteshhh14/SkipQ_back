package com.skipq.backend.service;

import com.skipq.backend.dto.AddToCartRequest;
import com.skipq.backend.dto.CartItemResponse;
import com.skipq.backend.dto.CartResponse;
import com.skipq.backend.exception.BadRequestException;
import com.skipq.backend.exception.ResourceNotFoundException;
import com.skipq.backend.model.Cart;
import com.skipq.backend.model.CartItem;
import com.skipq.backend.model.Product;
import com.skipq.backend.repository.CartItemRepository;
import com.skipq.backend.repository.CartRepository;
import com.skipq.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        Cart cart = cartRepository.findByUserIdAndStoreId(request.getUserId(), request.getStoreId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(request.getUserId())
                                .storeId(request.getStoreId())
                                .build()));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cartId(cart.getId())
                    .productId(product.getId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return buildCartResponse(cart);
    }

    public CartResponse getCart(String userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if (carts.isEmpty()) {
            throw new ResourceNotFoundException("No cart found for user: " + userId);
        }
        // Return the most recently created cart (last in list)
        Cart cart = carts.get(carts.size() - 1);
        return buildCartResponse(cart);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
        }
        cartItemRepository.deleteById(cartItemId);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            Product p = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .cartItemId(item.getId())
                    .productId(p.getId())
                    .productName(p.getName())
                    .price(p.getPrice())
                    .quantity(item.getQuantity())
                    .imageUrl(p.getImageUrl())
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .storeId(cart.getStoreId())
                .items(itemResponses)
                .totalAmount(total)
                .build();
    }
}
