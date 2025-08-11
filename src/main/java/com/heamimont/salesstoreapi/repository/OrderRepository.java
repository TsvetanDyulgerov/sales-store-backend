package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.Order;
import com.heamimont.salesstoreapi.model.OrderProduct;
import com.heamimont.salesstoreapi.model.Product;
import com.heamimont.salesstoreapi.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findOrdersByUser_Username(String username);

}
