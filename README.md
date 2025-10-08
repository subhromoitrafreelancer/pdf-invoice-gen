# Invoice Generator API

This Spring Boot application provides an API for generating PDF invoices based on user data. It uses Apache PDFBox to convert HTML templates into downloadable PDF files.

## Features

- Web UI for invoice generation with live form validation
- Generate PDF invoices from HTML templates
- Store invoice data in a database
- Configurable invoice limit per user (default: 10)
- Download generated PDFs locally
- REST API for programmatic access

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Apache PDFBox 2.0.33
- H2 Database (for development)
- Maven
- Thymeleaf (for HTML templates)

## Project Structure

```
invoice-generator/
├── src/
│   ├── main/
│   │   ├── java/com/invoice/generator/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── InvoiceGeneratorApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   └── invoice-template.html
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── invoice-styles.css
│   │       └── application.properties
│   └── test/
│       └── java/com/invoice/generator/
├── pom.xml
└── README.md
```

## Setup Instructions

### Development Environment

1. Clone the repository
2. Make sure you have JDK 17 and Maven installed
3. Configure application.properties for your environment
4. Run the application using Maven:

```bash
mvn spring-boot:run
```

### Production Environment

1. Build the application JAR:

```bash
mvn clean package
```

2. Configure production settings in application-prod.properties
3. Run the application with production profile:

```bash
java -jar target/invoice-generator-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Usage

### Web Interface

Access the web UI at http://localhost:8080/ after starting the application. The interface provides:

- User-friendly form with all invoice fields
- Dynamic invoice item rows (add/remove items)
- Automatic calculation of item amounts and totals
- Real-time validation
- Automatic PDF download on success

### REST API

#### Generate Invoice

**Endpoint**: `POST /api/invoices`

**Request Body Example**:
```json
{
  "userId": "12345",
  "billerDetails": {
    "name": "Company ABC",
    "address": "123 Business St, Suite 100, City, Country - 12345",
    "pan": "ABCDE1234F",
    "gst": "22AAAAA0000A1Z5"
  },
  "clientDetails": {
    "name": "XYZ Corporation",
    "address": "456 Corporate Ave, City, Country - 54321",
    "pan": "PQRST5678G",
    "gst": "27BBBBB1111B1Z4"
  },
  "invoiceDetails": {
    "invoiceNumber": "INV-2023-001",
    "invoiceDate": "2023-11-20",
    "dueDate": "2023-12-20"
  },
  "items": [
    {
      "description": "Web Development Services",
      "days": 15,
      "unitCost": 5000.00,
      "amount": 75000.00
    },
    {
      "description": "UI/UX Design",
      "days": 5,
      "unitCost": 6000.00,
      "amount": 30000.00
    }
  ],
  "taxes": 18900.00,
  "adjustments": {
    "discount": 5000.00,
    "coupon": 1000.00
  },
  "tds": 2500.00,
  "totalAmount": 115400.00,
  "bankDetails": {
    "accountName": "Company ABC",
    "accountNumber": "1234567890",
    "bankName": "National Bank",
    "ifscCode": "NATL0001234",
    "branchName": "Main Branch"
  }
}
```

**Response**:
- HTTP 201: Invoice generated successfully with download link
- HTTP 400: Invalid request
- HTTP 409: Invoice already exists for the user

## Configuration

### application.properties

```properties
# Server configuration
server.port=8080

# Database configuration (H2 for development)
spring.datasource.url=jdbc:h2:mem:invoicedb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# Invoice PDF storage path
invoice.pdf.storage.path=./invoices
```

## Testing

Run the tests using Maven:

```bash
mvn test
```

## Docker Deployment

### Build and Run with Docker

```bash
# Build the Docker image
docker build -t pdf-invoice-gen:latest .

# Run the container
docker run -d \
  --name pdf-invoice-gen \
  -p 8080:8080 \
  -v $(pwd)/invoices:/app/invoices \
  pdf-invoice-gen:latest
```

### Using Docker Compose (Recommended)

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### Environment Variables

Override default configuration using environment variables:

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: docker)
- `SERVER_PORT`: Application port (default: 8080)
- `INVOICE_PDF_STORAGE_PATH`: PDF storage directory (default: /app/invoices)
- `INVOICE_MAX_PER_USER`: Maximum invoices per user (default: 10)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_H2_CONSOLE_ENABLED`: Enable H2 console (default: true)

### Health Check

The application includes a health check endpoint for monitoring:

```bash
curl http://localhost:8080/actuator/health
```
