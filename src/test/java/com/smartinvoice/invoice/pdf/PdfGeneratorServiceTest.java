package com.smartinvoice.invoice.pdf;

import com.lowagie.text.DocumentException;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfGeneratorServiceTest {

    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        pdfGeneratorService = new PdfGeneratorService();
    }

    @Test
    void shouldGeneratePdfFromInvoice() throws DocumentException {
        // Arrange
        Client client = Client.builder()
                .id(1L)
                .name("Emma Graphic Studio")
                .email("emma@studio.com")
                .companyName("Emma Designs")
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .name("Logo Design")
                .price(150.0)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Business Card Design")
                .price(100.0)
                .build();

        Invoice invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV-1001")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .client(client)
                .products(List.of(product1, product2))
                .totalAmount(250.0)
                .build();

        // Act
        byte[] pdf = pdfGeneratorService.generateInvoicePdf(invoice);

        // Assert
        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);

        // Optionally verify it's a valid PDF header
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
        byte[] header = new byte[4];
        inputStream.read(header, 0, 4);
        assertThat(new String(header)).isEqualTo("%PDF");
    }
}
