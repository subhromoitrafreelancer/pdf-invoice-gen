package com.anansu.pdfgen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
public class TemplateController {

    private final TemplateService templateService;

    /**
     * Get all available invoice templates
     * @return list of template metadata
     */
    @GetMapping
    public ResponseEntity<List<TemplateMetadata>> getAllTemplates() {
        log.info("Fetching all available templates");
        List<TemplateMetadata> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }
}
