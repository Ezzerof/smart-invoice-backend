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

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    @Scheduled(cron = "0 * * * * *") // every minute
    public void runReminderTask() {
        List<Invoice> unpaidInvoices = invoiceRepository.findAllUnpaidWithProductsAndReminders()
                .stream()
                .filter(this::shouldSendReminder)
                .toList();

        unpaidInvoices.forEach(invoice -> {
            try {
                byte[] pdf = pdfGeneratorService.generateInvoicePdf(invoice);

                String email = invoice.getClient().getEmail();
                String subject = "Payment Reminder: Invoice " + invoice.getInvoiceNumber();
                String body = buildReminderBody(invoice);

                emailService.sendInvoiceEmail(email, subject, body, pdf, "Invoice-" + invoice.getInvoiceNumber() + ".pdf");
                System.out.println("✅ Reminder email sent to: " + email);

                // Track that we sent today
                invoice.getReminderSentDates().add(LocalDate.now());
                invoiceRepository.save(invoice);
            } catch (Exception e) {
                System.err.println("Failed to process invoice ID: " + invoice.getId());
                e.printStackTrace();
            }
        });
    }

    private boolean shouldSendReminder(Invoice invoice) {
        LocalDate dueDate = invoice.getDueDate();
        LocalDate today = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(dueDate, today);

        boolean isReminderDay = daysDiff == -3 || daysDiff == 1 || daysDiff == 5 || daysDiff == 10;
        boolean alreadySentToday = invoice.getReminderSentDates().contains(today);

        return isReminderDay && !alreadySentToday;
    }


    private String buildReminderBody(Invoice invoice) {
        return "Dear " + invoice.getClient().getName() + ",\n\n"
                + "This is a reminder for your invoice #" + invoice.getInvoiceNumber()
                + " due on " + invoice.getDueDate() + ". Please find the invoice attached.\n\n"
                + "Best regards,\nSmartInvoice Team";
    }
}

