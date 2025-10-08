package com.anansu.pdfgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private String message;
    private String invoiceNumber;
    private String downloadUrl;
    private String templateId;
}
