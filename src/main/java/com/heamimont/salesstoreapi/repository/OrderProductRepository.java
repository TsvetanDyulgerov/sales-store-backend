package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.OrderProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductKey> {
    List<OrderProduct> findByOrderId(Long orderId);
    List<OrderProduct> findByProductId(Long productId);
    List<OrderProduct> findByProductIdAndProductQuantityGreaterThan(Long productId, int quantity);
    List<OrderProduct> findByOrderIdAndProductQuantityGreaterThanEqual(Long orderId, int minQuantity);
}
