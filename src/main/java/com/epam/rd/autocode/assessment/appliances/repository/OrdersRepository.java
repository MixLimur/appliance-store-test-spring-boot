package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query(value = """
            SELECT o FROM Orders o 
            LEFT JOIN o.employee e 
            WHERE o.client.email = :clientEmail 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') OR 
                 LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """,
            countQuery = """
            SELECT COUNT(o) FROM Orders o 
            LEFT JOIN o.employee e 
            WHERE o.client.email = :clientEmail 
            AND (:keyword IS NULL OR :keyword = '' OR 
                 CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') OR 
                 LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Orders> findByClientEmailWithFilter(@Param("clientEmail") String clientEmail,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    @Query(value = """
            SELECT o FROM Orders o 
            LEFT JOIN o.client c 
            LEFT JOIN o.employee e 
            WHERE (:keyword IS NULL OR :keyword = '' OR 
                 CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') OR 
                 LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                 LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """,
            countQuery = """
            SELECT COUNT(o) FROM Orders o 
            LEFT JOIN o.client c 
            LEFT JOIN o.employee e 
            WHERE (:keyword IS NULL OR :keyword = '' OR 
                 CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') OR 
                 LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                 LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Orders> findAllWithFilter(@Param("keyword") String keyword, Pageable pageable);
}
