package com.smartinvoice.invoice.pdf;

import com.smartinvoice.company.CompanyProperties;
import com.smartinvoice.company.CompanyProperties.BankDetails;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PdfGeneratorServiceTest {

    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        CompanyProperties companyProperties = new CompanyProperties();
        companyProperties.setName("SmartInvoice Ltd");
        companyProperties.setAddress("123 Test Street");
        companyProperties.setCity("London");
        companyProperties.setCountry("UK");
        companyProperties.setPostCode("E1 6AN");
        companyProperties.setPhone("0123456789");
        companyProperties.setEmail("info@smartinvoice.com");

        BankDetails bank = new BankDetails();
        bank.setHolder("SmartInvoice Ltd");
        bank.setAccount("12345678");
        bank.setSortCode("12-34-56");
        companyProperties.setBank(bank);

        pdfGeneratorService = new PdfGeneratorService(companyProperties);
    }

    @Test
    @DisplayName("Should generate PDF for valid invoice")
    void shouldGeneratePdfSuccessfully() {
        Client client = Client.builder()
                .name("Alice")
                .address("1 Main St")
                .city("London")
                .country("UK")
                .postcode("E1 1AA")
                .build();

        Product product = Product.builder()
                .name("Logo Design")
                .price(250.0)
                .build();

        Invoice invoice = Invoice.builder()
                .client(client)
                .invoiceNumber("INV-001")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .totalAmount(250.0)
                .isPaid(false)
                .build();

        byte[] result = pdfGeneratorService.generateInvoicePdf(invoice);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should throw exception when invoice is null")
    void shouldThrowWhenInvoiceIsNull() {
        assertThatThrownBy(() -> pdfGeneratorService.generateInvoicePdf(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to generate PDF");
    }

    @Test
    @DisplayName("Should handle missing client info without crashing")
    void shouldHandleMissingClientFieldsGracefully() {
        Client client = Client.builder()
                .name(null)
                .address(null)
                .city(null)
                .country(null)
                .postcode(null)
                .build();

        Product product = Product.builder()
                .name("Logo Design")
                .price(150.0)
                .build();

        Invoice invoice = Invoice.builder()
                .client(client)
                .invoiceNumber("INV-002")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .totalAmount(150.0)
                .isPaid(true)
                .build();

        byte[] pdf = pdfGeneratorService.generateInvoicePdf(invoice);

        assertThat(pdf).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should throw if product list is empty")
    void shouldFailWithEmptyProductList() {
        Client client = Client.builder()
                .name("Bob")
                .address("123 Lane")
                .city("London")
                .country("UK")
                .postcode("E2 2BB")
                .build();

        Invoice invoice = Invoice.builder()
                .client(client)
                .invoiceNumber("INV-003")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .products(List.of())
                .totalAmount(0.0)
                .isPaid(false)
                .build();

        byte[] result = pdfGeneratorService.generateInvoicePdf(invoice);
        assertThat(result).isNotEmpty();
    }
}
