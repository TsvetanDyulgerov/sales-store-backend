package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.Order;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    List<Order> findOrdersByUser_Username(String username);

    @Override
    Optional<Order> findById(@NonNull UUID orderId);
}
