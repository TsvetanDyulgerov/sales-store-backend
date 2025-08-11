package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.OrderProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductKey> {
}
