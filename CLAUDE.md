# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application that generates PDF invoices from HTML templates. It enforces a one-invoice-per-user policy and stores invoice data in an H2 database. The application uses Flying Saucer to convert Thymeleaf-rendered HTML into PDFs.

## Build and Run Commands

### Development
```bash
# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Build without running tests
mvn clean package -DskipTests

# Build with tests
mvn clean package
```

### Docker Deployment
```bash
# Build Docker image
docker build -t pdf-invoice-gen:latest .

# Run with Docker Compose (recommended)
docker-compose up -d

# Run with Docker directly
docker run -d -p 8080:8080 -v $(pwd)/invoices:/app/invoices pdf-invoice-gen:latest

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Accessing the Application
- Web UI: http://localhost:8080/ (default landing page with invoice generation form)
- API: http://localhost:8080/api/invoices
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:invoicedb, username: sa, password: empty)
- Health Check: http://localhost:8080/actuator/health

## Architecture

### Core Components

**InvoiceService** (src/main/java/com/anansu/pdfgen/InvoiceService.java:18)
- Main business logic for invoice generation
- Validates uniqueness constraints (userId and invoiceNumber)
- Calculates subtotal from line items
- Coordinates database persistence and PDF generation
- Two key validations at src/main/java/com/anansu/pdfgen/InvoiceService.java:37 and :42

**PdfGenerationService** (src/main/java/com/anansu/pdfgen/PdfGenerationService.java:28)
- Handles HTML-to-PDF conversion using Flying Saucer (not PDFBox directly)
- Processes Thymeleaf template at src/main/resources/templates/invoice-template.html
- Creates output directory if needed
- Date formatting via DATE_FORMATTER at src/main/java/com/anansu/pdfgen/PdfGenerationService.java:31

**HomeController** (src/main/java/com/anansu/pdfgen/HomeController.java)
- Serves the web UI form as the default landing page at /
- Uses Thymeleaf template: src/main/resources/templates/index.html

**InvoiceController** (src/main/java/com/anansu/pdfgen/InvoiceController.java:20)
- REST endpoints: POST /api/invoices and GET /api/invoices/download/{invoiceNumber}
- Returns 201 Created on successful generation
- Returns 409 Conflict if invoice already exists for user or invoice number
- Serves PDF files with proper Content-Disposition headers

### Data Model

**Invoice Entity** (src/main/java/com/anansu/pdfgen/Invoice.java:20)
- Enforces unique constraints on userId and invoiceNumber
- Uses @Embedded for BillerDetails, ClientDetails, and BankDetails
- Uses @AttributeOverrides for ClientDetails to avoid column name conflicts
- OneToMany relationship with InvoiceItem (EAGER fetch)
- Auto-timestamps createdAt via @PrePersist

### Key Business Rules

1. **Configurable invoice limit per user**: Maximum number of invoices per user is configurable via `invoice.max-per-user` property (default: 10). Checked at InvoiceService.java:40 via `invoiceRepository.countByUserId()`
2. **Unique invoice numbers**: Checked at InvoiceService.java:47 via `invoiceRepository.existsByInvoiceNumber()`
3. **Subtotal calculation**: Sum of all item amounts at InvoiceService.java:56
4. **PDF storage**: Files stored in `./invoices/` directory (configurable via `invoice.pdf.storage.path`)

### Web UI

Located at src/main/resources/templates/index.html:
- Built with Cirrus UI CSS framework (CDN)
- jQuery for JavaScript functionality (CDN)
- Dynamic invoice item management (add/remove rows)
- Automatic calculations for amounts and totals
- AJAX submission to /api/invoices
- Success/error notifications
- Automatic PDF download on success

### Template Processing Flow

1. InvoiceRequest DTO validated by Jakarta Validation
2. InvoiceService creates Invoice entity from request
3. Invoice saved to database (gets ID assigned)
4. PdfGenerationService processes Thymeleaf template with Invoice data
5. Flying Saucer renders HTML to PDF
6. PDF path stored in Invoice.pdfPath
7. Response returns download URL

## Configuration

Key properties in src/main/resources/application.properties:
- `server.port`: Default 8080
- `invoice.pdf.storage.path`: Where PDFs are saved (default: ./invoices)
- `invoice.max-per-user`: Maximum invoices allowed per user (default: 10)
- `spring.jpa.hibernate.ddl-auto=update`: Schema auto-generated from entities
- `spring.h2.console.enabled=true`: H2 console accessible for debugging

## Testing

Tests use Spring Boot Test framework. Key test classes:
- InvoiceServiceTest: Business logic tests
- InvoiceControllerTest: REST API integration tests

## Dependencies

- Spring Boot 3.4.4
- Java 17
- Flying Saucer 9.3.1 (HTML to PDF conversion)
- Apache PDFBox 2.0.33 (referenced but Flying Saucer used for rendering)
- Thymeleaf (template engine)
- H2 Database (in-memory)
- Lombok (boilerplate reduction)
- JSoup 1.16.1 (HTML parsing)

## Common Development Patterns

When modifying invoice generation:
1. Update DTOs (InvoiceRequest/Response) if changing API contract
2. Update Invoice entity if adding database fields
3. Update Thymeleaf template if changing PDF layout
4. Remember to handle @Embedded entities with @AttributeOverrides if adding similar structures
5. PDF generation is transactional - failures roll back database changes
