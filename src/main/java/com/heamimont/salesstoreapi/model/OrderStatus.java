package com.heamimont.salesstoreapi.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Order has been created but not yet processed"),
    IN_PROGRESS("Order is being processed"),
    DONE("Order has been completed");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

}
