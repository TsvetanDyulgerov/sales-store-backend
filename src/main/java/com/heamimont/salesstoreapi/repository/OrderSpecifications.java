package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDate;

public class OrderSpecifications {

    public static Specification<Order> hasProductName(String productName) {
        return (root, query, cb) -> {
            // Join orderProducts and then product, filter by product name (LIKE %productName%)
            Join<Object, Object> orderProductsJoin = root.join("orderProducts");
            Join<Object, Object> productJoin = orderProductsJoin.join("product");
            return cb.like(cb.lower(productJoin.get("name")), "%" + productName.toLowerCase() + "%");
        };
    }

    public static Specification<Order> hasUsername(String username) {
        return (root, query, cb) -> {
            return cb.equal(cb.lower(root.get("user").get("username")), username.toLowerCase());
        };
    }

    public static Specification<Order> orderDateAfter(LocalDate startDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("orderDate"), startDate);
    }

    public static Specification<Order> orderDateBefore(LocalDate endDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("orderDate"), endDate);
    }
}
