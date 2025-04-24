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
public class ClientDetailsDto {

    @NotBlank(message = "Client name is required")
    private String name;

    @NotBlank(message = "Client address is required")
    private String address;

    @NotBlank(message = "Client PAN is required")
    private String pan;

    private String gst;
}
