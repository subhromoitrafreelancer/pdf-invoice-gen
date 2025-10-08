package com.anansu.pdfgen;

import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;

import java.io.IOException;

@Slf4j
public class CustomFontResolver {

    /**
     * Register custom fonts with the ITextFontResolver
     * @param fontResolver the font resolver to register fonts with
     */
    public static void registerFonts(ITextFontResolver fontResolver) {
        try {
            // Register Roboto font family (for modern template)
            registerFont(fontResolver, "static/fonts/Roboto-Regular.ttf");
            registerFont(fontResolver, "static/fonts/Roboto-Bold.ttf");
            registerFont(fontResolver, "static/fonts/Roboto-Italic.ttf");

            // Register Open Sans font family (for minimal template)
            registerFont(fontResolver, "static/fonts/OpenSans-Regular.ttf");
            registerFont(fontResolver, "static/fonts/OpenSans-Bold.ttf");
            registerFont(fontResolver, "static/fonts/OpenSans-Italic.ttf");

            // Register Montserrat font family (for modern template accents)
            registerFont(fontResolver, "static/fonts/Montserrat-Regular.ttf");
            registerFont(fontResolver, "static/fonts/Montserrat-Bold.ttf");

            log.info("Custom fonts registered successfully");
        } catch (Exception e) {
            log.warn("Failed to register some custom fonts, falling back to system fonts: {}", e.getMessage());
        }
    }

    /**
     * Register a single font file
     * @param fontResolver the font resolver
     * @param fontPath the classpath relative path to the font file
     */
    private static void registerFont(ITextFontResolver fontResolver, String fontPath) {
        try {
            ClassPathResource fontResource = new ClassPathResource(fontPath);
            if (fontResource.exists()) {
                String absolutePath = fontResource.getFile().getAbsolutePath();
                fontResolver.addFont(absolutePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                log.debug("Registered font: {}", fontPath);
            } else {
                log.debug("Font file not found (skipping): {}", fontPath);
            }
        } catch (IOException e) {
            log.debug("Could not register font {} (skipping): {}", fontPath, e.getMessage());
        } catch (Exception e) {
            log.warn("Error registering font {} (skipping): {}", fontPath, e.getMessage());
        }
    }
}
