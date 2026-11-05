package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Manufacturer manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PowerType powerType;

    private String characteristic;

    private String description;

    private Integer power;

    @Column(nullable = false)
    private BigDecimal price;
}
