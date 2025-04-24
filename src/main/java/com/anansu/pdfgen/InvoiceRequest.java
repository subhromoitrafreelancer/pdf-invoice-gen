package com.anansu.pdfgen;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @Valid
    @NotNull(message = "Biller details are required")
    private BillerDetailsDto billerDetails;

    @Valid
    @NotNull(message = "Client details are required")
    private ClientDetailsDto clientDetails;

    @Valid
    @NotNull(message = "Invoice details are required")
    private InvoiceDetailsDto invoiceDetails;

    @Valid
    @NotEmpty(message = "At least one invoice item is required")
    @Size(min = 1, message = "At least one invoice item is required")
    private List<InvoiceItemDto> items;

    @NotNull(message = "Taxes amount is required")
    private BigDecimal taxes;

    @Valid
    private AdjustmentsDto adjustments;

    private BigDecimal tds;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Valid
    @NotNull(message = "Bank details are required")
    private BankDetailsDto bankDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDetailsDto {
        @NotBlank(message = "Invoice number is required")
        private String invoiceNumber;

        @NotNull(message = "Invoice date is required")
        private LocalDate invoiceDate;

        @NotNull(message = "Due date is required")
        private LocalDate dueDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdjustmentsDto {
        private BigDecimal discount;
        private BigDecimal coupon;
    }
}
