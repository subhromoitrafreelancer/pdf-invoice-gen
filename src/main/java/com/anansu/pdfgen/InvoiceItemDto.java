package com.anansu.pdfgen;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {

    @NotBlank(message = "Item description is required")
    private String description;

    @Min(value = 0, message = "Days must be a positive number")
    private int days;

    @NotNull(message = "Unit cost is required")
    @Min(value = 0, message = "Unit cost must be a positive number")
    private BigDecimal unitCost;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be a positive number")
    private BigDecimal amount;
}
