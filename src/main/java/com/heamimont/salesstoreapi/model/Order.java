package com.heamimont.salesstoreapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Order entity representing a customer's order in the sales store.
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Setter
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Getter
    @Setter
    @NotNull
    @PositiveOrZero
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Getter
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Getter
    @Setter
    @NotEmpty()
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

}