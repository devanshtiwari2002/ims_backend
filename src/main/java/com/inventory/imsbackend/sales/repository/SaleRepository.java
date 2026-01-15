package com.inventory.imsbackend.sales.repository;

import com.inventory.imsbackend.sales.entity.Sale;
import com.inventory.imsbackend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // For admin see everything with pagination
    Page<Sale> findAll(Pageable pageable);

    //for staff: see only there own sales
    Page<Sale> findBySeller(User seller, Pageable pageable);

    // Custom query to find the last invoice number to increment it
    @Query("SELECT s.invoiceNumber FROM Sale s ORDER BY s.id DESC LIMIT 1")
    Optional<String> findLastInvoiceNumber();
}
