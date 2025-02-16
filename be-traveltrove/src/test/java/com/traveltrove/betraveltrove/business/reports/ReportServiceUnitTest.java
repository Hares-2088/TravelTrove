package com.traveltrove.betraveltrove.business.reports;

import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.business.report.ReportServiceImpl;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.utils.reports.CSVGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceUnitTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private CSVGenerator csvGenerator;

    @InjectMocks
    private ReportServiceImpl reportService;

    private List<Payment> samplePayments;

    @BeforeEach
    void setUp() {
        samplePayments = List.of(
                Payment.builder()
                        .paymentId("1")
                        .bookingId("booking1")
                        .amount(10000L)
                        .currency("usd")
                        .paymentDate(LocalDateTime.of(2024, 2, 1, 12, 0))
                        .status("succeeded")
                        .stripePaymentId("stripe1")
                        .build(),
                Payment.builder()
                        .paymentId("2")
                        .bookingId("booking2")
                        .amount(20000L)
                        .currency("usd")
                        .paymentDate(LocalDateTime.of(2024, 2, 15, 14, 30))
                        .status("failed")
                        .stripePaymentId("stripe2")
                        .build()
        );
    }

    @Test
    void whenGenerateMonthlyReport_thenReturnCSV() throws IOException {
        int year = 2024;
        int month = 2;
        String periodType = "monthly";

        when(paymentService.getPaymentsByPeriod(year, month))
                .thenReturn(Flux.fromIterable(samplePayments));

        when(csvGenerator.generateRevenueCSV(samplePayments, periodType))
                .thenReturn(new ByteArrayInputStream("mock csv data".getBytes()));

        StepVerifier.create(reportService.generatePaymentRevenueReport(periodType, year, month))
                .expectNextMatches(csv -> csv.available() > 0)
                .verifyComplete();

        verify(paymentService).getPaymentsByPeriod(year, month);
        verify(csvGenerator).generateRevenueCSV(samplePayments, periodType);
    }

    @Test
    void whenGenerateYearlyReport_thenReturnCSV() throws IOException {
        int year = 2024;
        String periodType = "yearly";

        when(paymentService.getPaymentsByPeriod(year, null))
                .thenReturn(Flux.fromIterable(samplePayments));

        when(csvGenerator.generateRevenueCSV(samplePayments, periodType))
                .thenReturn(new ByteArrayInputStream("mock csv data".getBytes()));

        StepVerifier.create(reportService.generatePaymentRevenueReport(periodType, year, null))
                .expectNextMatches(csv -> csv.available() > 0)
                .verifyComplete();

        verify(paymentService).getPaymentsByPeriod(year, null);
        verify(csvGenerator).generateRevenueCSV(samplePayments, periodType);
    }

    @Test
    void whenNoPaymentsFound_thenReturnEmptyCSV() throws IOException {
        int year = 2024;
        int month = 3;
        String periodType = "monthly";

        when(paymentService.getPaymentsByPeriod(year, month))
                .thenReturn(Flux.empty());

        when(csvGenerator.generateRevenueCSV(List.of(), periodType))
                .thenReturn(new ByteArrayInputStream("".getBytes()));

        StepVerifier.create(reportService.generatePaymentRevenueReport(periodType, year, month))
                .expectNextMatches(csv -> csv.available() == 0)
                .verifyComplete();

        verify(paymentService).getPaymentsByPeriod(year, month);
        verify(csvGenerator).generateRevenueCSV(List.of(), periodType);
    }
}
