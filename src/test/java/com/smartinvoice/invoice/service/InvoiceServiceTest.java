package com.smartinvoice.invoice.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.ProductDto;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.pdf.PdfGeneratorService;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    private InvoiceRepository invoiceRepository;
    private ClientRepository clientRepository;
    private ProductRepository productRepository;
    private PdfGeneratorService pdfGeneratorService;
    private AuditLogService auditLogService;
    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        invoiceRepository = mock(InvoiceRepository.class);
        clientRepository = mock(ClientRepository.class);
        productRepository = mock(ProductRepository.class);
        pdfGeneratorService = mock(PdfGeneratorService.class);
        auditLogService = mock(AuditLogService.class);
        invoiceService = new InvoiceService(invoiceRepository, clientRepository, productRepository, pdfGeneratorService, null, auditLogService);
    }

    @Test
    @DisplayName("Should create invoice successfully")
    void shouldCreateInvoiceSuccessfully() {
        Client client = Client.builder().id(1L).name("Alice").invoices(new ArrayList<>()).build();
        Product product1 = Product.builder().id(10L).name("Logo Design").build();
        Product product2 = Product.builder().id(11L).name("Website Mockup").build();

        InvoiceRequestDto dto = new InvoiceRequestDto(1L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), false,
                List.of(new ProductDto(10L, 1, 150.0), new ProductDto(11L, 1, 350.0)));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findAllById(any())).thenReturn(List.of(product1, product2));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = invoiceService.createInvoice(dto);

        assertThat(result.invoiceNumber()).isEqualTo("INV-001");
        assertThat(result.clientId()).isEqualTo(1L);
        assertThat(result.productIds()).containsExactlyInAnyOrder(10L, 11L);
        assertThat(result.totalAmount()).isEqualTo(500.0);
    }

    @Test
    @DisplayName("Should fail if client not found")
    void shouldFailIfClientNotFound() {
        InvoiceRequestDto dto = new InvoiceRequestDto(99L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), false,
                List.of(new ProductDto(10L, 1, 150.0)));

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.createInvoice(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found");
    }

    @Test
    @DisplayName("Should fail if products list is null")
    void shouldFailIfProductsNull() {
        InvoiceRequestDto dto = new InvoiceRequestDto(1L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), false, null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));

        assertThatThrownBy(() -> invoiceService.createInvoice(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invoice must contain at least one product");
    }

    @Test
    @DisplayName("Should fail if product IDs are invalid")
    void shouldFailIfProductIdsInvalid() {
        InvoiceRequestDto dto = new InvoiceRequestDto(1L, "INV-001", LocalDate.now(), LocalDate.now().plusDays(7), false,
                List.of(new ProductDto(null, 1, 150.0)));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));

        assertThatThrownBy(() -> invoiceService.createInvoice(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No valid product IDs provided");
    }

    @Test
    @DisplayName("Should get invoice by ID")
    void shouldGetInvoiceById() {
        Invoice invoice = Invoice.builder().id(1L).invoiceNumber("INV-001").client(Client.builder().id(1L).name("Alice").email("alice@mail.com").build()).products(List.of()).totalAmount(0.0).isPaid(false).build();
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        var result = invoiceService.getInvoiceById(1L);

        assertThat(result.invoiceNumber()).isEqualTo("INV-001");
    }

    @Test
    @DisplayName("Should throw if invoice by ID not found")
    void shouldThrowIfInvoiceNotFoundById() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getInvoiceById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    @DisplayName("Should delete invoice")
    void shouldDeleteInvoice() {
        Invoice invoice = Invoice.builder().id(5L).invoiceNumber("INV-005").build();
        when(invoiceRepository.findById(5L)).thenReturn(Optional.of(invoice));

        invoiceService.deleteInvoice(5L);

        verify(invoiceRepository).delete(invoice);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent invoice")
    void shouldFailToDeleteMissingInvoice() {
        when(invoiceRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.deleteInvoice(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    @DisplayName("Should mark invoice as paid")
    void shouldMarkInvoiceAsPaid() {
        Invoice invoice = Invoice.builder().id(2L).isPaid(false).build();
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(invoice));

        invoiceService.markAsPaid(2L);

        assertThat(invoice.getIsPaid()).isTrue();
        verify(invoiceRepository).save(invoice);
    }

    @Test
    @DisplayName("Should throw when marking non-existent invoice as paid")
    void shouldFailToMarkMissingInvoicePaid() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.markAsPaid(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    @DisplayName("Should generate invoice PDF")
    void shouldGenerateInvoicePdf() {
        Invoice invoice = Invoice.builder().id(3L).build();
        when(invoiceRepository.findById(3L)).thenReturn(Optional.of(invoice));
        when(pdfGeneratorService.generateInvoicePdf(invoice)).thenReturn(new byte[]{1, 2, 3});

        byte[] result = invoiceService.getInvoicePdf(3L);

        assertThat(result).isEqualTo(new byte[]{1, 2, 3});
    }

    @Test
    @DisplayName("Should throw if PDF generation invoice not found")
    void shouldFailToGeneratePdfForMissingInvoice() {
        when(invoiceRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getInvoicePdf(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }
}
