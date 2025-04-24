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
public class ClientDetails {

    @Column(name = "client_name", nullable = false)
    private String name;

    @Column(name = "client_address", nullable = false)
    private String address;

    @Column(name = "client_pan", nullable = false)
    private String pan;

    @Column(name = "client_gst")
    private String gst;
}
