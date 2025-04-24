package com.anansu.pdfgen;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillerDetailsDto {

    @NotBlank(message = "Biller name is required")
    private String name;

    @NotBlank(message = "Biller address is required")
    private String address;

    @NotBlank(message = "Biller PAN is required")
    private String pan;

    private String gst;
}
