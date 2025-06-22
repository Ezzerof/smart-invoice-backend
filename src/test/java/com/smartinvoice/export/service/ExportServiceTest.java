package com.smartinvoice.export.service;

import com.smartinvoice.client.dto.ClientFilterRequest;
import com.smartinvoice.export.dto.ExportClientFilterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class ExportServiceTest {

    private ExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportService();
    }

    @Test
    @DisplayName("Should map ExportClientFilterRequest to ClientFilterRequest correctly")
    void shouldMapToClientFilterSuccessfully() {
        ExportClientFilterRequest input = new ExportClientFilterRequest("Alice", "DesignCo", "London", "UK");

        ClientFilterRequest result = exportService.mapToClientFilter(input);

        assertThat(result).isNotNull();
        assertThat(result.keyword()).isEqualTo("Alice DesignCo");
        assertThat(result.city()).isEqualTo("London");
        assertThat(result.country()).isEqualTo("UK");
    }

    @Test
    @DisplayName("Should return null keyword if both name and company are null")
    void shouldReturnNullKeywordIfBothNull() {
        ExportClientFilterRequest input = new ExportClientFilterRequest(null, null, "London", "UK");

        ClientFilterRequest result = exportService.mapToClientFilter(input);

        assertThat(result.keyword()).isNull();
    }

    @Test
    @DisplayName("Should trim extra spaces in keyword")
    void shouldTrimSpacesInKeyword() {
        ExportClientFilterRequest input = new ExportClientFilterRequest("Alice", null, "London", "UK");

        ClientFilterRequest result = exportService.mapToClientFilter(input);

        assertThat(result.keyword()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should not throw if only one date is provided")
    void shouldPassWhenOnlyOneDateProvided() {
        assertThatCode(() ->
                exportService.validateInvoiceDates(LocalDate.now(), null)
        ).doesNotThrowAnyException();

        assertThatCode(() ->
                exportService.validateInvoiceDates(null, LocalDate.now())
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should not throw if issueDate is before dueDate")
    void shouldValidateCorrectDateRange() {
        LocalDate issue = LocalDate.of(2024, 1, 1);
        LocalDate due = LocalDate.of(2024, 1, 31);

        assertThatCode(() -> exportService.validateInvoiceDates(issue, due))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw if issueDate is after dueDate")
    void shouldThrowForInvalidDateRange() {
        LocalDate issue = LocalDate.of(2024, 2, 1);
        LocalDate due = LocalDate.of(2024, 1, 1);

        assertThatThrownBy(() -> exportService.validateInvoiceDates(issue, due))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start date must be before end date");
    }
}
