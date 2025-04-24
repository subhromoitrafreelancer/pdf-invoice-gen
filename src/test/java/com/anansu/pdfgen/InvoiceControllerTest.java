package com.anansu.pdfgen;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService invoiceService;

    private InvoiceRequest invoiceRequest;
    private InvoiceResponse invoiceResponse;

    @BeforeEach
    void setUp() {
        // Create a test invoice request
        List<InvoiceItemDto> items = new ArrayList<>();
        items.add(new InvoiceItemDto("Web Development Services", 15, new BigDecimal("5000.00"), new BigDecimal("75000.00")));
        items.add(new InvoiceItemDto("UI/UX Design", 5, new BigDecimal("6000.00"), new BigDecimal("30000.00")));

        InvoiceRequest.AdjustmentsDto adjustments = new InvoiceRequest.AdjustmentsDto(
                new BigDecimal("5000.00"), new BigDecimal("1000.00"));

        invoiceRequest = InvoiceRequest.builder()
                .userId("test-user-123")
                .billerDetails(new BillerDetailsDto("Company ABC", "123 Business St, City", "ABCDE1234F", "22AAAAA0000A1Z5"))
                .clientDetails(new ClientDetailsDto("XYZ Corporation", "456 Corporate Ave, City", "PQRST5678G", "27BBBBB1111B1Z4"))
                .invoiceDetails(new InvoiceRequest.InvoiceDetailsDto("INV-2023-001", LocalDate.now(), LocalDate.now().plusDays(30)))
                .items(items)
                .taxes(new BigDecimal("18900.00"))
                .adjustments(adjustments)
                .tds(new BigDecimal("2500.00"))
                .totalAmount(new BigDecimal("115400.00"))
                .bankDetails(new BankDetailsDto("Company ABC", "1234567890", "National Bank", "NATL0001234", "Main Branch"))
                .build();

        // Create a test invoice response
        invoiceResponse = InvoiceResponse.builder()
                .message("Invoice generated successfully")
                .invoiceNumber("INV-2023-001")
                .downloadUrl("/api/invoices/download/INV-2023-001")
                .build();
    }

    @Test
    void generateInvoice_Success() throws Exception {
        // Arrange
        when(invoiceService.generateInvoice(any(InvoiceRequest.class))).thenReturn(invoiceResponse);

        // Act & Assert
        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Invoice generated successfully"))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-2023-001"))
                .andExpect(jsonPath("$.downloadUrl").value("/api/invoices/download/INV-2023-001"));
    }

    @Test
    void generateInvoice_InvoiceAlreadyExists() throws Exception {
        // Arrange
        when(invoiceService.generateInvoice(any(InvoiceRequest.class)))
                .thenThrow(new InvoiceAlreadyExistsException("An invoice already exists for user: test-user-123"));

        // Act & Assert
        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVOICE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("An invoice already exists for user: test-user-123"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void downloadInvoice_Success() throws Exception {
        // Create a temporary test file
        Path tempFile = Files.createTempFile("test-invoice", ".pdf");
        try {
            // Write some content to the file
            String dummyContent = "This is a test PDF content";
            Files.write(tempFile, dummyContent.getBytes());

            // Arrange
            when(invoiceService.getPdfPath(anyString())).thenReturn(tempFile.toString());

            // Act & Assert
            mockMvc.perform(get("/api/invoices/download/INV-2023-001"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=invoice_INV-2023-001.pdf"))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        } finally {
            // Clean up
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void downloadInvoice_NotFound() throws Exception {
        // Arrange
        when(invoiceService.getPdfPath(anyString())).thenReturn("/non/existent/path/invoice.pdf");

        // Act & Assert
        mockMvc.perform(get("/api/invoices/download/INV-2023-001"))
                .andExpect(status().isNotFound());
    }
}
