package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Set<OrderRow> orderRowSet = new HashSet<>();

    private Boolean approved;

    public void addOrderRow(OrderRow row) {
        orderRowSet.add(row);
    }

    public void removeOrderRow(OrderRow row) {
        orderRowSet.remove(row);
    }
}
