package com.smartinvoice.invoice.util;

import com.smartinvoice.invoice.entity.Invoice;
import com.smartinvoice.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InvoiceStatusUpdater {

    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 * * * * *") // every day at 8:00 AM (cron = "0 0 8 * * *")
    public void updateInvoiceStatuses() {
        LocalDate today = LocalDate.now();

        // Mark newly overdue invoices
        invoiceRepository.findByDueDateBeforeAndStatus(today, Invoice.InvoiceStatus.PENDING)
                .forEach(invoice -> {
                    invoice.setStatus(Invoice.InvoiceStatus.OVERDUE);
                    invoice.setOverdueSince(today);
                    invoiceRepository.save(invoice);
                });
    }
}
