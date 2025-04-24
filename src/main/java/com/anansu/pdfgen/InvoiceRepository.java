package com.anansu.pdfgen;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find an invoice by user ID
     *
     * @param userId the ID of the user
     * @return Optional containing the invoice if found, empty otherwise
     */
    Optional<Invoice> findByUserId(String userId);

    /**
     * Check if an invoice exists for a given user ID
     *
     * @param userId the ID of the user
     * @return true if invoice exists, false otherwise
     */
    boolean existsByUserId(String userId);

    /**
     * Find an invoice by invoice number
     *
     * @param invoiceNumber the invoice number
     * @return Optional containing the invoice if found, empty otherwise
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Check if an invoice exists with the given invoice number
     *
     * @param invoiceNumber the invoice number to check
     * @return true if invoice exists, false otherwise
     */
    boolean existsByInvoiceNumber(String invoiceNumber);
}
