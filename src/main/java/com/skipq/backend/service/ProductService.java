package com.skipq.backend.service;

import com.skipq.backend.dto.ProductResponse;
import com.skipq.backend.exception.ResourceNotFoundException;
import com.skipq.backend.model.Product;
import com.skipq.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with barcode: " + barcode));
        return toResponse(product);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .storeId(p.getStoreId())
                .name(p.getName())
                .price(p.getPrice())
                .barcode(p.getBarcode())
                .imageUrl(p.getImageUrl())
                .stockQuantity(p.getStockQuantity())
                .build();
    }
}
