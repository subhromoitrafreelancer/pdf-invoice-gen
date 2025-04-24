package com.anansu.pdfgen;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * Generate a new invoice
     *
     * @param request the invoice request
     * @return the invoice response
     */
    @PostMapping
    public ResponseEntity<InvoiceResponse> generateInvoice(@Valid @RequestBody InvoiceRequest request) {
        log.info("Received request to generate invoice for user: {}", request.getUserId());
        InvoiceResponse response = invoiceService.generateInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Download a generated invoice PDF
     *
     * @param invoiceNumber the invoice number
     * @return the PDF file
     */
    @GetMapping("/download/{invoiceNumber}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable String invoiceNumber) {
        log.info("Received request to download invoice: {}", invoiceNumber);
        String pdfPath = invoiceService.getPdfPath(invoiceNumber);
        File file = new File(pdfPath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String filename = "invoice_" + invoiceNumber.replace("/", "_") + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
