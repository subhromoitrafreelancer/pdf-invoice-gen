package com.anansu.pdfgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMetadata {

    private String id;
    private String name;
    private String description;
    private String templatePath;
    private String thumbnailUrl;
    private String fontFamily;

    /**
     * Get the full template path for Thymeleaf
     * @return the template path without .html extension
     */
    public String getThymeleafTemplatePath() {
        return "invoices/" + id + "/invoice-template";
    }
}
