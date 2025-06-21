package com.smartinvoice.export.service;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.export.dto.ExportClientFilterRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExportService {
    public ClientFilterRequest mapToClientFilter(ExportClientFilterRequest req) {
        String keyword = buildKeyword(req.name(), req.companyName());
        return new ClientFilterRequest(keyword, req.city(), req.country(), null);
    }

    public void validateInvoiceDates(LocalDate issueDate, LocalDate dueDate) {
        if (issueDate != null && dueDate != null && issueDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    private String buildKeyword(String name, String companyName) {
        if (name == null && companyName == null) return null;
        StringBuilder sb = new StringBuilder();
        if (name != null) sb.append(name).append(" ");
        if (companyName != null) sb.append(companyName);
        return sb.toString().trim();
    }
}
