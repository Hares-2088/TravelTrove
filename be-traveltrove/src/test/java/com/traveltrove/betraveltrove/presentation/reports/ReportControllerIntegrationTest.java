package com.traveltrove.betraveltrove.presentation.reports;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReportControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentRepository paymentRepository;

    private final int YEAR = 2024;
    private final int MONTH = 2;

    private final Payment payment1 = Payment.builder()
            .paymentId("1")
            .bookingId("booking1")
            .amount(10000L)
            .currency("usd")
            .paymentDate(LocalDateTime.of(2024, 2, 10, 14, 30))
            .status("succeeded")
            .stripePaymentId("stripe1")
            .build();

    private final Payment payment2 = Payment.builder()
            .paymentId("2")
            .bookingId("booking2")
            .amount(20000L)
            .currency("usd")
            .paymentDate(LocalDateTime.of(2024, 2, 20, 18, 0))
            .status("failed")
            .stripePaymentId("stripe2")
            .build();

    @BeforeEach
    public void setupDB() {
        // Clean database before each test
        StepVerifier.create(paymentRepository.deleteAll()).verifyComplete();

        // Add sample payments
        Publisher<Payment> setupDB = Flux.just(payment1, payment2)
                .flatMap(paymentRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGenerateMonthlyReport_thenReturnCSV() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/reports/revenue")
                        .queryParam("periodType", "monthly")
                        .queryParam("year", YEAR)
                        .queryParam("month", MONTH)
                        .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(CONTENT_DISPOSITION,
                        "attachment; filename=revenue-report-" + YEAR + "-" + String.format("%02d", MONTH) + ".csv")
                .expectBody()
                .consumeWith(response -> {
                    byte[] responseBody = response.getResponseBody();
                    assertTrue(responseBody != null);
                    String csvContent = new String(responseBody);

                    // 🔍 Debugging: Print CSV content
                    System.out.println("CSV Output:\n" + csvContent);

                    // Check header as produced by your CSV generator
                    assertTrue(csvContent.contains("Payment ID,Amount (USD),Currency,Status,Payment Date"));
                    // Check payment lines with raw amounts and formatted dates
                    assertTrue(csvContent.contains("1,10000,usd,succeeded,2024-02-10 14:30:00"));
                    assertTrue(csvContent.contains("2,20000,usd,failed,2024-02-20 18:00:00"));
                });

        // Verify database contains the correct data
        StepVerifier.create(paymentRepository.findAll())
                .expectNextMatches(payment -> payment.getPaymentId().equals(payment1.getPaymentId()))
                .expectNextMatches(payment -> payment.getPaymentId().equals(payment2.getPaymentId()))
                .verifyComplete();
    }

    @Test
    void whenGenerateYearlyReport_thenReturnCSV() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/reports/revenue")
                        .queryParam("periodType", "yearly")
                        .queryParam("year", YEAR)
                        .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(CONTENT_DISPOSITION,
                        "attachment; filename=revenue-report-" + YEAR + ".csv")
                .expectBody()
                .consumeWith(response -> {
                    byte[] responseBody = response.getResponseBody();
                    assertTrue(responseBody != null);
                    String csvContent = new String(responseBody);
                    // Check CSV header
                    assertTrue(csvContent.contains("Payment ID,Amount (USD),Currency,Status,Payment Date"));
                    // Check payment rows
                    assertTrue(csvContent.contains("1,10000,usd,succeeded,2024-02-10 14:30:00"));
                    assertTrue(csvContent.contains("2,20000,usd,failed,2024-02-20 18:00:00"));
                });

        // Verify database still has the payments
        StepVerifier.create(paymentRepository.findAll())
                .expectNextMatches(payment -> payment.getPaymentId().equals(payment1.getPaymentId()))
                .expectNextMatches(payment -> payment.getPaymentId().equals(payment2.getPaymentId()))
                .verifyComplete();
    }

    @Test
    void whenGenerateReport_withInvalidPeriodType_thenReturnBadRequest() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/reports/revenue")
                        .queryParam("periodType", "invalid")
                        .queryParam("year", YEAR)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenNoPaymentsFound_thenReturnCSVWithNoPaymentRows() {
        // Clean database before running test
        StepVerifier.create(paymentRepository.deleteAll()).verifyComplete();

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/reports/revenue")
                        .queryParam("periodType", "monthly")
                        .queryParam("year", YEAR)
                        .queryParam("month", MONTH)
                        .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    byte[] responseBody = response.getResponseBody();
                    assertTrue(responseBody != null);
                    String csvContent = new String(responseBody);

                    // For an empty DB, your CSV generator still prints the header, a blank line, summary, and a total of 0.
                    assertTrue(csvContent.contains("Payment ID,Amount (USD),Currency,Status,Payment Date"));
                    assertTrue(csvContent.contains("Summary,,,,")); // Summary row
                    assertTrue(csvContent.contains("TOTAL,0,USD,,"));  // Total row showing 0 amount
                });

        // Verify database is empty
        StepVerifier.create(paymentRepository.findAll())
                .verifyComplete();
    }
}
