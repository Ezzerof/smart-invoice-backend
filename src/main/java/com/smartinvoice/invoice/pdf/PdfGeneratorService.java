package com.smartinvoice.invoice.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.smartinvoice.invoice.entity.Invoice;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Invoice: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Client: " + invoice.getClient().getName()));
            document.add(new Paragraph("Issue Date: " + invoice.getIssueDate()));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate()));
            document.add(new Paragraph("Total: £" + invoice.getTotalAmount()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Products:"));
            invoice.getProducts().forEach(product ->
                    document.add(new Paragraph("- " + product.getName() + " (£" + product.getPrice() + ")"))
            );

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
