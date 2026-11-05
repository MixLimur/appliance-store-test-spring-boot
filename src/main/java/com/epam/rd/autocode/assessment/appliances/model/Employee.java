package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee extends User {

    @Column(nullable = false)
    private String department;

    public Employee(Long id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
    }
}
