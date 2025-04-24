package com.anansu.pdfgen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${invoice.pdf.storage.path}")
    private String pdfStoragePath;

    /**
     * Initialize the PDF storage directory on application startup
     *
     * @return a File object representing the storage directory
     * @throws RuntimeException if the directory cannot be created
     */
    @Bean
    public File pdfStorageDirectory() {
        try {
            Path path = Paths.get(pdfStoragePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return path.toFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF storage directory: " + e.getMessage(), e);
        }
    }
}
