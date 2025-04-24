package com.anansu.pdfgen;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillerDetails {

    @Column(name = "biller_name", nullable = false)
    private String name;

    @Column(name = "biller_address", nullable = false)
    private String address;

    @Column(name = "biller_pan", nullable = false)
    private String pan;

    @Column(name = "biller_gst")
    private String gst;
}
