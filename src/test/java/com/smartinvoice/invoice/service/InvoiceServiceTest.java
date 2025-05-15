package com.smartinvoice.invoice.service;

import com.smartinvoice.client.entity.Client;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import com.smartinvoice.product.entity.Product;
import com.smartinvoice.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    private InvoiceRepository invoiceRepository;
    private ClientRepository clientRepository;
    private ProductRepository productRepository;
    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        invoiceRepository = mock(InvoiceRepository.class);
        clientRepository = mock(ClientRepository.class);
        productRepository = mock(ProductRepository.class);
        invoiceService = new InvoiceService(invoiceRepository, clientRepository, productRepository);
    }

    @Test
    void shouldCreateInvoiceSuccessfully() {
        Client client = Client.builder().id(1L).name("Alice").build();
        Product product1 = Product.builder().id(10L).name("Logo Design").price(150.0).build();
        Product product2 = Product.builder().id(11L).name("Website Mockup").price(350.0).build();

        InvoiceRequestDto dto = new InvoiceRequestDto(
                "INV-001",
                LocalDate.now(),
                LocalDate.now().plusDays(15),
                1L,
                List.of(10L, 11L)
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(product1, product2));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = invoiceService.createInvoice(dto);

        assertThat(response.invoiceNumber()).isEqualTo("INV-001");
        assertThat(response.clientId()).isEqualTo(1L);
        assertThat(response.totalAmount()).isEqualTo(500.0);
        assertThat(response.productIds()).containsExactlyInAnyOrder(10L, 11L);
    }

    @Test
    void shouldThrowExceptionIfClientNotFound() {
        InvoiceRequestDto dto = new InvoiceRequestDto(
                "INV-002",
                LocalDate.now(),
                LocalDate.now().plusDays(15),
                99L,
                List.of(1L)
        );

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.createInvoice(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found");
    }

    @Test
    void shouldThrowExceptionIfInvoiceNotFoundById() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getInvoiceById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

    @Test
    void shouldDeleteInvoiceSuccessfully() {
        Invoice invoice = Invoice.builder().id(5L).invoiceNumber("INV-005").build();

        when(invoiceRepository.findById(5L)).thenReturn(Optional.of(invoice));

        invoiceService.deleteInvoice(5L);

        verify(invoiceRepository).delete(invoice);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentInvoice() {
        when(invoiceRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.deleteInvoice(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invoice not found");
    }

}
