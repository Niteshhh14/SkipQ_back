package com.skipq.backend.config;

import com.skipq.backend.model.Product;
import com.skipq.backend.model.Store;
import com.skipq.backend.repository.ProductRepository;
import com.skipq.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (storeRepository.count() == 0) {
            Store store1 = storeRepository.save(Store.builder()
                    .storeName("SkipQ Mart - MG Road")
                    .storeCode("STORE001")
                    .location("MG Road, Bangalore")
                    .build());

            Store store2 = storeRepository.save(Store.builder()
                    .storeName("SkipQ Superstore - Koramangala")
                    .storeCode("STORE002")
                    .location("Koramangala, Bangalore")
                    .build());

            // Products for Store 1
            productRepository.save(Product.builder()
                    .storeId(store1.getId())
                    .name("Amul Butter 500g")
                    .price(new BigDecimal("275.00"))
                    .barcode("8901063025318")
                    .imageUrl("https://placehold.co/200x200?text=Amul+Butter")
                    .stockQuantity(50)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store1.getId())
                    .name("Parle-G Biscuits 800g")
                    .price(new BigDecimal("60.00"))
                    .barcode("8901719114480")
                    .imageUrl("https://placehold.co/200x200?text=Parle-G")
                    .stockQuantity(100)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store1.getId())
                    .name("Tata Salt 1kg")
                    .price(new BigDecimal("24.00"))
                    .barcode("8901094000014")
                    .imageUrl("https://placehold.co/200x200?text=Tata+Salt")
                    .stockQuantity(200)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store1.getId())
                    .name("Colgate Toothpaste 200g")
                    .price(new BigDecimal("98.00"))
                    .barcode("8901314000017")
                    .imageUrl("https://placehold.co/200x200?text=Colgate")
                    .stockQuantity(80)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store1.getId())
                    .name("Maggi Noodles 70g")
                    .price(new BigDecimal("14.00"))
                    .barcode("8901058011030")
                    .imageUrl("https://placehold.co/200x200?text=Maggi")
                    .stockQuantity(300)
                    .build());

            // Products for Store 2
            productRepository.save(Product.builder()
                    .storeId(store2.getId())
                    .name("Fortune Sunflower Oil 1L")
                    .price(new BigDecimal("155.00"))
                    .barcode("8904094300012")
                    .imageUrl("https://placehold.co/200x200?text=Fortune+Oil")
                    .stockQuantity(60)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store2.getId())
                    .name("Surf Excel Detergent 500g")
                    .price(new BigDecimal("110.00"))
                    .barcode("8901030704345")
                    .imageUrl("https://placehold.co/200x200?text=Surf+Excel")
                    .stockQuantity(70)
                    .build());

            productRepository.save(Product.builder()
                    .storeId(store2.getId())
                    .name("Aashirvaad Atta 5kg")
                    .price(new BigDecimal("260.00"))
                    .barcode("8901725131003")
                    .imageUrl("https://placehold.co/200x200?text=Aashirvaad+Atta")
                    .stockQuantity(40)
                    .build());

            System.out.println("Sample data seeded: 2 stores, 8 products.");
        }
    }
}
