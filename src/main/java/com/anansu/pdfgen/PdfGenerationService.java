package com.anansu.pdfgen;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine;
    private final TemplateService templateService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Generate a PDF invoice using the given invoice data and save it to the specified path
     *
     * @param invoice the invoice data
     * @param templateId the template identifier to use
     * @param outputPath the path where the PDF will be saved
     * @throws IOException if there's an error writing to the file
     */
    public void generatePdf(Invoice invoice, String templateId, String outputPath) throws IOException {
        // Create directories if they don't exist
        Files.createDirectories(Paths.get(outputPath).getParent());

        // Resolve template path
        String templatePath = templateService.resolveTemplatePath(templateId);
        log.info("Using template: {} for invoice generation", templatePath);

        // Set up Thymeleaf context
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("dateFormatter", DATE_FORMATTER);

        // Process the template
        String htmlContent = templateEngine.process(templatePath, context);

        // Create PDF from HTML using Flying Saucer with custom fonts
        try (OutputStream outputStream = new FileOutputStream(outputPath)) {
            ITextRenderer renderer = new ITextRenderer();

            // Register custom fonts
            CustomFontResolver.registerFonts(renderer.getFontResolver());

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            log.info("PDF invoice generated successfully at: {} with template: {}", outputPath, templateId);
        } catch (Exception e) {
            log.error("Error generating PDF invoice with template: {}", templateId, e);
            throw new IOException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a PDF invoice using the default template (for backward compatibility)
     *
     * @param invoice the invoice data
     * @param outputPath the path where the PDF will be saved
     * @throws IOException if there's an error writing to the file
     */
    public void generatePdf(Invoice invoice, String outputPath) throws IOException {
        generatePdf(invoice, "default", outputPath);
    }
}
