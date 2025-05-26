package com.smartinvoice.invoice.scheduler;

import com.smartinvoice.invoice.email.EmailService;
import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.pdf.PdfGeneratorService;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    @Scheduled(cron = "0 * * * * *") // every day at 9:00 AM (cron = "0 0 9 * * *")
    public void runReminderTask() {
        // Combine both overdue AND unpaid pending invoices
        List<Invoice> invoicesToRemind = invoiceRepository.findAllUnpaidWithProductsAndReminders()
                .stream()
                .filter(invoice ->
                        invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE ||
                                invoice.getStatus() == Invoice.InvoiceStatus.PENDING
                )
                .filter(this::shouldSendReminder)
                .toList();

        invoicesToRemind.forEach(this::sendReminder);
    }

    private boolean shouldSendReminder(Invoice invoice) {
        LocalDate dueDate = invoice.getDueDate();
        LocalDate today = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(dueDate, today);

        if (invoice.getReminderSentDates().contains(today)) {
            return false;
        }

        // For PENDING invoices (before due date)
        if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING) {
            return daysDiff == -3; // Only send 3 days before due date
        }
        // For OVERDUE invoices (after due date)
        else if (invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE) {
            return Set.of(1L, 5L, 10L).contains(daysDiff);
        }

        return false;
    }

    private void sendReminder(Invoice invoice) {
        try {
            byte[] pdf = pdfGeneratorService.generateInvoicePdf(invoice);
            String email = invoice.getClient().getEmail();
            String subject = buildReminderSubject(invoice);
            String body = buildReminderBody(invoice);

            emailService.sendInvoiceEmail(email, subject, body, pdf,
                    "Invoice-" + invoice.getInvoiceNumber() + ".pdf");

            // Track reminder
            invoice.getReminderSentDates().add(LocalDate.now());
            invoiceRepository.save(invoice);

            System.out.println("✅ Reminder sent for invoice " + invoice.getInvoiceNumber());
        } catch (Exception e) {
            System.err.println("❌ Failed to process invoice ID: " + invoice.getId());
            e.printStackTrace();
        }
    }

    private String buildReminderSubject(Invoice invoice) {
        return invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE
                ? "URGENT: Overdue Invoice " + invoice.getInvoiceNumber()
                : "Upcoming Payment: Invoice " + invoice.getInvoiceNumber();
    }

    private String buildReminderBody(Invoice invoice) {
        String base = "Dear " + invoice.getClient().getName() + ",\n\n" +
                "Invoice #" + invoice.getInvoiceNumber() +
                " (" + invoice.getTotalAmount() + " GBP) ";

        if (invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE) {
            long daysOverdue = ChronoUnit.DAYS.between(invoice.getDueDate(), LocalDate.now());
            return base + "is " + daysOverdue + " day(s) overdue. Please make payment immediately.\n\n";
        } else {
            return base + "is due in 3 days on " + invoice.getDueDate() + ".\n\n";
        }
    }
}


