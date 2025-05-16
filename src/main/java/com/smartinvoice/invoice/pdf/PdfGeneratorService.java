package com.smartinvoice.invoice.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartinvoice.company.CompanyProperties;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.product.entity.Product;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service
public class PdfGeneratorService {

    private final CompanyProperties company;

    public PdfGeneratorService(CompanyProperties company) {
        this.company = company;
    }

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.DARK_GRAY);
            Font subFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font tableHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

            // Header section
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{3, 1});

            PdfPCell companyInfoCell = new PdfPCell();
            companyInfoCell.setBorder(Rectangle.NO_BORDER);
            companyInfoCell.addElement(new Paragraph(company.getName(), titleFont));
            companyInfoCell.addElement(new Paragraph(company.getAddress() + "\n" + company.getCity() + "\n" + company.getCountry() + "\n" + company.getPostCode(), subFont));
            companyInfoCell.addElement(new Paragraph(company.getPhone() + "\n" + company.getEmail(), subFont));
            headerTable.addCell(companyInfoCell);

            // Optional logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            // Image path:
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleToFit(80, 80);
            logoCell.addElement(logo);
            headerTable.addCell(logoCell);
            document.add(headerTable);

            document.add(new Paragraph(" ")); // spacing

            // Client Info & Invoice Details
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setSpacingBefore(10f);
            metaTable.setWidths(new float[]{1, 1});

            PdfPCell billedTo = new PdfPCell();
            billedTo.setBorder(Rectangle.NO_BORDER);
            billedTo.addElement(new Paragraph("BILLED TO", boldFont));
            billedTo.addElement(new Paragraph(invoice.getClient().getName(), subFont));
            billedTo.addElement(new Paragraph(invoice.getClient().getAddress(), subFont));
            billedTo.addElement(new Paragraph(invoice.getClient().getCity(), subFont));
            billedTo.addElement(new Paragraph(invoice.getClient().getCountry(), subFont));
            billedTo.addElement(new Paragraph(invoice.getClient().getPostcode(), subFont));
            metaTable.addCell(billedTo);

            PdfPCell invoiceDetails = new PdfPCell();
            invoiceDetails.setBorder(Rectangle.NO_BORDER);
            invoiceDetails.addElement(new Paragraph("Invoice", titleFont));
            invoiceDetails.addElement(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber(), subFont));
            invoiceDetails.addElement(new Paragraph("Date of Issue: " + invoice.getIssueDate(), subFont));
            metaTable.addCell(invoiceDetails);

            document.add(metaTable);
            document.add(new Paragraph(" "));

            // Product Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 2});
            table.setSpacingBefore(10f);

            Stream.of("Description", "Unit cost", "QTY", "Amount").forEach(header -> {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, tableHeaderFont));
                headerCell.setBackgroundColor(Color.GRAY);
                headerCell.setPadding(5);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            });

            for (Product product : invoice.getProducts()) {
                table.addCell(getTableCell(product.getName(), subFont));
                table.addCell(getTableCell("£" + product.getPrice(), subFont));
                table.addCell(getTableCell("1", subFont)); // default 1 for now
                table.addCell(getTableCell("£" + product.getPrice(), subFont));
            }

            document.add(table);

            // Summary Section
            document.add(new Paragraph(" "));

            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(50);
            summary.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summary.setWidths(new float[]{2, 1});

            summary.addCell(getRightCell("Subtotal:", boldFont));
            summary.addCell(getRightCell("£" + invoice.getTotalAmount(), subFont));

            summary.addCell(getRightCell("Discount:", boldFont));
            summary.addCell(getRightCell("£0.00", subFont));

            summary.addCell(getRightCell("INVOICE TOTAL:", boldFont));
            summary.addCell(getRightCell("£" + invoice.getTotalAmount(), boldFont));
            document.add(summary);


            // Bank details + Terms side-by-side
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20f);
            footerTable.setWidths(new float[]{1, 1});

            // Left cell: Bank details
            PdfPCell bankDetailsCell = new PdfPCell();
            bankDetailsCell.setBorder(Rectangle.NO_BORDER);
            bankDetailsCell.addElement(new Paragraph("BANK ACCOUNT DETAILS", boldFont));
            bankDetailsCell.addElement(new Paragraph("Account Holder: " + company.getBank().getHolder(), subFont));
            bankDetailsCell.addElement(new Paragraph("Account number: " + company.getBank().getAccount(), subFont));
            bankDetailsCell.addElement(new Paragraph("Sort-code: " + company.getBank().getSortCode(), subFont));

            // Right cell: Terms
            PdfPCell termsCell = new PdfPCell();
            termsCell.setBorder(Rectangle.NO_BORDER);
            termsCell.addElement(new Paragraph("TERMS", boldFont));
            termsCell.addElement(new Paragraph("Please pay invoice by " + invoice.getDueDate(), subFont));

            // Add cells to footer table
            footerTable.addCell(bankDetailsCell);
            footerTable.addCell(termsCell);
            document.add(footerTable);


            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private PdfPCell getTableCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell getRightCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        return cell;
    }
}

