package com.smartinvoice.invoice.service;

import com.smartinvoice.audit.service.AuditLogService;
import com.smartinvoice.client.repository.ClientRepository;
import com.smartinvoice.exception.ResourceNotFoundException;
import com.smartinvoice.export.dto.InvoiceFilterRequest;
import com.smartinvoice.invoice.dto.InvoiceSearchFilter;
import com.smartinvoice.invoice.dto.InvoiceRequestDto;
import com.smartinvoice.invoice.dto.InvoiceResponseDto;
import com.smartinvoice.invoice.email.EmailService;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.pdf.PdfGeneratorService;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import com.smartinvoice.product.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final AuditLogService auditLogService;

    public InvoiceResponseDto createInvoice(InvoiceRequestDto dto) {
        var client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        if (dto.products() == null || dto.products().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one product");
        }

        var productIds = dto.products().stream()
                .map(item -> item.productId())
                .filter(id -> id != null)
                .toList();

        if (productIds.isEmpty()) {
            throw new IllegalArgumentException("No valid product IDs provided");
        }

        var products = productRepository.findAllById(productIds);

        double totalAmount = dto.products().stream()
                .mapToDouble(item -> item.price() * item.quantity())
                .sum();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(dto.invoiceNumber() != null ? dto.invoiceNumber() : generateNextInvoiceNumber())
                .issueDate(dto.issueDate() != null ? dto.issueDate() : LocalDate.now())
                .dueDate(dto.dueDate())
                .client(client)
                .products(products)
                .totalAmount(totalAmount)
                .isPaid(dto.isPaid() != null ? dto.isPaid() : false)
                .build();


        client.getInvoices().add(invoice);

        Invoice saved = invoiceRepository.save(invoice);

        auditLogService.log("CREATE", "Invoice", String.valueOf(saved.getId()));

        return mapToDto(saved);
    }

    private String generateNextInvoiceNumber() {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        String prefix = "INV-" + currentYear + "-";

        // Find the last invoice number for the current year
        List<Invoice> yearInvoices = invoiceRepository.findByInvoiceNumberStartingWithOrderByInvoiceNumberDesc(prefix);

        if (!yearInvoices.isEmpty()) {
            String lastNumber = yearInvoices.get(0).getInvoiceNumber(); // e.g., INV-2025-0042
            String[] parts = lastNumber.split("-");
            int lastSeq = Integer.parseInt(parts[2]);
            return prefix + String.format("%04d", lastSeq + 1);
        }

        // If no invoices yet for this year
        return prefix + "0001";
    }


    public List<InvoiceResponseDto> getFilteredInvoices(InvoiceSearchFilter filter) {
        Specification<Invoice> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.search() != null && !filter.search().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("invoiceNumber")), "%" + filter.search().toLowerCase() + "%"));
            }
            if (filter.isPaid() != null) {
                predicates.add(cb.equal(root.get("isPaid"), filter.isPaid()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return invoiceRepository.findAll(spec)
                .stream()
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

        auditLogService.log("DELETE", "Invoice", String.valueOf(invoice.getId()));
    }

    public void markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDate.now());
        invoice.setIsPaid(true);

        invoiceRepository.save(invoice);
    }

    private InvoiceResponseDto mapToDto(Invoice invoice) {
        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getClient().getName(),
                invoice.getClient().getEmail(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getClient().getId(),
                invoice.getProducts().stream().map(p -> p.getId()).collect(Collectors.toList()),
                invoice.getIsPaid(),
                invoice.getStatus()
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

        auditLogService.log("EMAIL_SENT", "Invoice", String.valueOf(invoice.getId()));
    }

    private Specification<Invoice> withFilters(InvoiceFilterRequest filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.issueDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), filters.issueDate()));
            }
            if (filters.dueDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), filters.dueDate()));
            }
            if (filters.clientId() != null) {
                predicates.add(cb.equal(root.get("client").get("id"), filters.clientId()));
            }
            if (filters.isPaid() != null) {
                predicates.add(cb.equal(root.get("isPaid"), filters.isPaid()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public void exportInvoicesToCsv(HttpServletResponse response, InvoiceFilterRequest filters) throws IOException {
        List<Invoice> invoices = invoiceRepository.findAll(withFilters(filters));

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=invoices.csv");

        try (Writer writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Invoice Number", "Issue Date", "Due Date", "Client Name", "Total Amount", "Is Paid"))) {

            for (Invoice invoice : invoices) {
                csvPrinter.printRecord(
                        invoice.getId(),
                        invoice.getInvoiceNumber(),
                        invoice.getIssueDate(),
                        invoice.getDueDate(),
                        invoice.getClient().getName(),
                        invoice.getTotalAmount(),
                        invoice.getIsPaid() ? "Yes" : "No"
                );
            }
        }
    }
}
