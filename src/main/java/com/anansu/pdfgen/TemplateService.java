package com.anansu.pdfgen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TemplateService {

    private final List<TemplateMetadata> availableTemplates;

    public TemplateService() {
        this.availableTemplates = new ArrayList<>();
        initializeTemplates();
    }

    /**
     * Initialize available templates with metadata
     */
    private void initializeTemplates() {
        // Default/Classic template
        availableTemplates.add(TemplateMetadata.builder()
                .id("default")
                .name("Classic Invoice")
                .description("Traditional professional invoice layout with clean design")
                .templatePath("invoices/default/invoice-template")
                .thumbnailUrl("/thumbnails/default.svg")
                .fontFamily("Arial")
                .build());

        // Modern template
        availableTemplates.add(TemplateMetadata.builder()
                .id("modern")
                .name("Modern Invoice")
                .description("Contemporary design with bold typography and vibrant colors")
                .templatePath("invoices/modern/invoice-template")
                .thumbnailUrl("/thumbnails/modern.svg")
                .fontFamily("Roboto")
                .build());

        // Minimal template
        availableTemplates.add(TemplateMetadata.builder()
                .id("minimal")
                .name("Minimal Invoice")
                .description("Minimalist design with elegant spacing and light aesthetics")
                .templatePath("invoices/minimal/invoice-template")
                .thumbnailUrl("/thumbnails/minimal.svg")
                .fontFamily("Open Sans")
                .build());

        log.info("Initialized {} invoice templates", availableTemplates.size());
    }

    /**
     * Get all available templates
     * @return list of all template metadata
     */
    public List<TemplateMetadata> getAllTemplates() {
        return new ArrayList<>(availableTemplates);
    }

    /**
     * Get template by ID
     * @param templateId the template identifier
     * @return Optional containing the template metadata if found
     */
    public Optional<TemplateMetadata> getTemplateById(String templateId) {
        return availableTemplates.stream()
                .filter(template -> template.getId().equals(templateId))
                .findFirst();
    }

    /**
     * Get template by ID or return default template
     * @param templateId the template identifier
     * @return the template metadata
     */
    public TemplateMetadata getTemplateByIdOrDefault(String templateId) {
        if (templateId == null || templateId.isBlank()) {
            return getDefaultTemplate();
        }

        return getTemplateById(templateId)
                .orElseGet(this::getDefaultTemplate);
    }

    /**
     * Get the default template
     * @return the default template metadata
     */
    public TemplateMetadata getDefaultTemplate() {
        return availableTemplates.stream()
                .filter(template -> template.getId().equals("default"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Default template not found"));
    }

    /**
     * Resolve Thymeleaf template path for a given template ID
     * @param templateId the template identifier
     * @return the Thymeleaf template path
     */
    public String resolveTemplatePath(String templateId) {
        return getTemplateByIdOrDefault(templateId).getThymeleafTemplatePath();
    }
}
