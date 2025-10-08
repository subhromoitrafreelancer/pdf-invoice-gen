package com.anansu.pdfgen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PdfGenerationService pdfGenerationService;

    @InjectMocks
    private InvoiceService invoiceService;

    private InvoiceRequest invoiceRequest;

    @BeforeEach
    void setUp() {
        // Set the PDF storage path and max invoices per user through reflection
        try {
            var field = InvoiceService.class.getDeclaredField("pdfStoragePath");
            field.setAccessible(true);
            field.set(invoiceService, "./test-invoices");

            var maxField = InvoiceService.class.getDeclaredField("maxInvoicesPerUser");
            maxField.setAccessible(true);
            maxField.set(invoiceService, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    }

    @Test
    void generateInvoice_Success() throws Exception {
        // Arrange
        when(invoiceRepository.countByUserId(anyString())).thenReturn(5L);
        when(invoiceRepository.existsByInvoiceNumber(anyString())).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });
        doNothing().when(pdfGenerationService).generatePdf(any(Invoice.class), anyString());

        // Act
        InvoiceResponse response = invoiceService.generateInvoice(invoiceRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Invoice generated successfully", response.getMessage());
        assertEquals("INV-2023-001", response.getInvoiceNumber());
        assertEquals("/api/invoices/download/INV-2023-001", response.getDownloadUrl());

        // Verify
        verify(invoiceRepository, times(1)).countByUserId("test-user-123");
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-2023-001");
        verify(invoiceRepository, times(2)).save(any(Invoice.class));
        verify(pdfGenerationService, times(1)).generatePdf(any(Invoice.class), anyString());
    }

    @Test
    void generateInvoice_UserReachedMaxLimit() throws IOException {
        // Arrange
        when(invoiceRepository.countByUserId(anyString())).thenReturn(10L);

        // Act & Assert
        assertThrows(InvoiceAlreadyExistsException.class, () -> {
            invoiceService.generateInvoice(invoiceRequest);
        });

        // Verify
        verify(invoiceRepository, times(1)).countByUserId("test-user-123");
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(pdfGenerationService, never()).generatePdf(any(Invoice.class), anyString());
    }

    @Test
    void generateInvoice_InvoiceNumberAlreadyExists() throws IOException {
        // Arrange
        when(invoiceRepository.countByUserId(anyString())).thenReturn(5L);
        when(invoiceRepository.existsByInvoiceNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(InvoiceAlreadyExistsException.class, () -> {
            invoiceService.generateInvoice(invoiceRequest);
        });

        // Verify
        verify(invoiceRepository, times(1)).countByUserId("test-user-123");
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-2023-001");
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(pdfGenerationService, never()).generatePdf(any(Invoice.class), anyString());
    }
}
