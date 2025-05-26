package com.smartinvoice.invoice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.smartinvoice.client.entity.Client;
import com.smartinvoice.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private LocalDate issueDate;

    private LocalDate dueDate;

    private double totalAmount;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "overdue_since")
    private LocalDate overdueSince;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "invoice_products",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @ElementCollection
    @CollectionTable(name = "invoice_reminders", joinColumns = @JoinColumn(name = "invoice_id"))
    @Column(name = "reminder_date")
    private Set<LocalDate> reminderSentDates = new HashSet<>();

    public enum InvoiceStatus {
        PENDING,       // Not yet due
        PAID,          // Payment received
        OVERDUE,       // Past due date
        PARTIALLY_PAID // For future use
    }

    // Ensure status stays in sync with isPaid
    @PreUpdate
    @PrePersist
    private void syncStatus() {
        if (Boolean.TRUE.equals(isPaid)) {
            this.status = InvoiceStatus.PAID;
            this.paidDate = LocalDate.now();
        } else if (this.status != InvoiceStatus.OVERDUE) {
            this.status = InvoiceStatus.PENDING;
        }
    }

}
