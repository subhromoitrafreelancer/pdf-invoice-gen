package com.anansu.pdfgen;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PdfGenerationService pdfGenerationService;

    @Value("${invoice.pdf.storage.path}")
    private String pdfStoragePath;

    /**
     * Generate a new invoice from the provided request
     *
     * @param request the invoice details
     * @return the response containing the invoice details and download link
     * @throws InvoiceAlreadyExistsException if an invoice already exists for the user
     * @throws InvoiceGenerationException if there's an error generating the invoice
     */
    @Transactional
    public InvoiceResponse generateInvoice(InvoiceRequest request) {
        // Check if an invoice already exists for this user
        if (invoiceRepository.existsByUserId(request.getUserId())) {
            throw new InvoiceAlreadyExistsException("An invoice already exists for user: " + request.getUserId());
        }

        // Check if an invoice with this number already exists
        if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceDetails().getInvoiceNumber())) {
            throw new InvoiceAlreadyExistsException("An invoice with number " +
                    request.getInvoiceDetails().getInvoiceNumber() + " already exists");
        }

        // Create invoice entity from request
        Invoice invoice = createInvoiceFromRequest(request);

        // Calculate subtotal
        BigDecimal subtotal = invoice.getItems().stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubtotal(subtotal);

        // Save invoice to database
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Generate PDF
        try {
            String fileName = "invoice_" + savedInvoice.getInvoiceNumber().replace("/", "_") + ".pdf";
            Path pdfPath = Paths.get(pdfStoragePath, fileName);

            // Generate PDF
            pdfGenerationService.generatePdf(savedInvoice, pdfPath.toString());

            // Update invoice with PDF path
            savedInvoice.setPdfPath(pdfPath.toString());
            invoiceRepository.save(savedInvoice);

            // Create response
            return InvoiceResponse.builder()
                    .message("Invoice generated successfully")
                    .invoiceNumber(savedInvoice.getInvoiceNumber())
                    .downloadUrl("/api/invoices/download/" + savedInvoice.getInvoiceNumber())
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice: {}", savedInvoice.getInvoiceNumber(), e);
            throw new InvoiceGenerationException("Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Create an Invoice entity from the request
     *
     * @param request the invoice request
     * @return the invoice entity
     */
    private Invoice createInvoiceFromRequest(InvoiceRequest request) {
        return Invoice.builder()
                .userId(request.getUserId())
                .invoiceNumber(request.getInvoiceDetails().getInvoiceNumber())
                .invoiceDate(request.getInvoiceDetails().getInvoiceDate())
                .dueDate(request.getInvoiceDetails().getDueDate())
                .billerDetails(BillerDetails.builder()
                        .name(request.getBillerDetails().getName())
                        .address(request.getBillerDetails().getAddress())
                        .pan(request.getBillerDetails().getPan())
                        .gst(request.getBillerDetails().getGst())
                        .build())
                .clientDetails(ClientDetails.builder()
                        .name(request.getClientDetails().getName())
                        .address(request.getClientDetails().getAddress())
                        .pan(request.getClientDetails().getPan())
                        .gst(request.getClientDetails().getGst())
                        .build())
                .items(request.getItems().stream()
                        .map(item -> InvoiceItem.builder()
                                .description(item.getDescription())
                                .days(item.getDays())
                                .unitCost(item.getUnitCost())
                                .amount(item.getAmount())
                                .build())
                        .collect(Collectors.toList()))
                .taxes(request.getTaxes())
                .discount(request.getAdjustments() != null ?
                        request.getAdjustments().getDiscount() : BigDecimal.ZERO)
                .coupon(request.getAdjustments() != null ?
                        request.getAdjustments().getCoupon() : BigDecimal.ZERO)
                .tds(request.getTds() != null ? request.getTds() : BigDecimal.ZERO)
                .totalAmount(request.getTotalAmount())
                .bankDetails(BankDetails.builder()
                        .accountName(request.getBankDetails().getAccountName())
                        .accountNumber(request.getBankDetails().getAccountNumber())
                        .bankName(request.getBankDetails().getBankName())
                        .ifscCode(request.getBankDetails().getIfscCode())
                        .branchName(request.getBankDetails().getBranchName())
                        .build())
                .build();
    }

    /**
     * Get the path to the PDF file for a given invoice number
     *
     * @param invoiceNumber the invoice number
     * @return the path to the PDF file
     * @throws RuntimeException if the invoice is not found
     */
    public String getPdfPath(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(Invoice::getPdfPath)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
    }
}
