package com.anansu.pdfgen;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Embedded
    private BillerDetails billerDetails;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "client_name")),
            @AttributeOverride(name = "address", column = @Column(name = "client_address")),
            @AttributeOverride(name = "pan", column = @Column(name = "client_pan")),
            @AttributeOverride(name = "gst", column = @Column(name = "client_gst"))
    })
    private ClientDetails clientDetails;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceItem> items;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal taxes;

    @Column(nullable = false)
    private BigDecimal discount;

    @Column
    private BigDecimal coupon;

    @Column(nullable = false)
    private BigDecimal tds;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Embedded
    private BankDetails bankDetails;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String pdfPath;

    @Column(nullable = false)
    @lombok.Builder.Default
    private String templateId = "default";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (templateId == null || templateId.isBlank()) {
            templateId = "default";
        }
    }
}
