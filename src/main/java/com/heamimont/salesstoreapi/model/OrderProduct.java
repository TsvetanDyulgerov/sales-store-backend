package com.heamimont.salesstoreapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "order_products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderProduct {

    public OrderProduct(Order order, Product product, Integer productQuantity) {
        this.order = order;
        this.product = product;
        this.productQuantity = productQuantity;
        this.id = new OrderProductKey(order.getId(), product.getId());
    }

    @EmbeddedId
    private OrderProductKey id = new OrderProductKey();

    @ManyToOne
    @NotNull
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @NotNull
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Positive
    @Column(nullable = false)
    private Integer productQuantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderProduct that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
