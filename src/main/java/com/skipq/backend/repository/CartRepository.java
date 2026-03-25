package com.skipq.backend.repository;

import com.skipq.backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStoreId(String userId, Long storeId);
    List<Cart> findByUserId(String userId);
}
