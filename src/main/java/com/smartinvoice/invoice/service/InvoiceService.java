package com.smartinvoice.invoice.service;

import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.email.EmailService;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.pdf.PdfGeneratorService;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import com.smartinvoice.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    public InvoiceResponseDto createInvoice(InvoiceRequestDto dto) {
        var client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        var products = productRepository.findAllById(dto.productIds());

        double totalAmount = products.stream()
                .mapToDouble(p -> p.getPrice())
                .sum();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(dto.invoiceNumber())
                .issueDate(dto.issueDate())
                .dueDate(dto.dueDate())
                .client(client)
                .products(products)
                .totalAmount(totalAmount)
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        return mapToDto(saved);
    }

    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        return mapToDto(invoice);
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        invoiceRepository.delete(invoice);
    }

    private InvoiceResponseDto mapToDto(Invoice invoice) {
        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getClient().getId(),
                invoice.getProducts().stream().map(p -> p.getId()).collect(Collectors.toList())
        );
    }

    public byte[] getInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        return pdfGeneratorService.generateInvoicePdf(invoice);
    }

    public void emailInvoiceToClient(Long invoiceId) {
        byte[] pdf = getInvoicePdf(invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        String email = invoice.getClient().getEmail();
        String subject = "Invoice: " + invoice.getInvoiceNumber();
        String body = "Dear " + invoice.getClient().getName() + ",\n\nPlease find attached your invoice.";

        emailService.sendInvoiceEmail(email, subject, body, pdf, "Invoice-" + invoice.getInvoiceNumber() + ".pdf");
    }

}
