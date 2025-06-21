package com.smartinvoice.invoice.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should send email with PDF attachment successfully")
    void shouldSendEmailSuccessfully() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        byte[] pdf = new byte[]{1, 2, 3};

        emailService.sendInvoiceEmail(
                "client@example.com",
                "Invoice #001",
                "Please find attached your invoice.",
                pdf,
                "invoice.pdf"
        );

        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should handle empty PDF attachment gracefully")
    void shouldSendEmailWithEmptyPdf() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        byte[] emptyPdf = new byte[0];

        emailService.sendInvoiceEmail(
                "client@example.com",
                "Empty PDF Test",
                "PDF has no content",
                emptyPdf,
                "empty-invoice.pdf"
        );

        verify(mailSender).send(mimeMessage);
    }
}
