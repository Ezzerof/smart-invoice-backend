package com.smartinvoice.invoice.repository;

import com.smartinvoice.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    @Query("""
                SELECT DISTINCT i FROM Invoice i
                LEFT JOIN FETCH i.products
                LEFT JOIN FETCH i.reminderSentDates
                WHERE i.isPaid = false
            """)
    List<Invoice> findAllUnpaidWithProductsAndReminders();

    List<Invoice> findByDueDateBeforeAndStatus(LocalDate date, Invoice.InvoiceStatus status);

    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    List<Invoice> findByOverdueSinceBefore(LocalDate date);

    List<Invoice> findByInvoiceNumberStartingWithOrderByInvoiceNumberDesc(String prefix);

}
