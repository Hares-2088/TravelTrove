package com.traveltrove.betraveltrove.business.reports;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.business.report.ReportServiceImpl;
import com.traveltrove.betraveltrove.business.review.ReviewService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.utils.reports.CSVGenerator;
import com.traveltrove.betraveltrove.utils.reports.PDFGenerator;
import com.traveltrove.betraveltrove.utils.reports.ReportFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceUnitTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private PackageService packageService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CSVGenerator csvGenerator;

    @Mock
    private PDFGenerator pdfGenerator;

    @Mock
    private ReportFormatter reportFormatter;

    @InjectMocks
    private ReportServiceImpl reportService;

    private List<BookingResponseModel> sampleBookings;

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
        sampleBookings = List.of(
                BookingResponseModel.builder()
                        .bookingId("B001")
                        .packageId("PKG1")
                        .bookingDate(LocalDate.of(2024, 2, 10))
                        .status(BookingStatus.BOOKING_CONFIRMED)
                        .build(),
                BookingResponseModel.builder()
                        .bookingId("B002")
                        .packageId("PKG2")
                        .bookingDate(LocalDate.of(2024, 2, 15))
                        .status(BookingStatus.BOOKING_CONFIRMED)
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

    @Test
    void whenGenerateMonthlyBookingReportPDF_thenReturnPDF() {
        int year = 2024;
        int month = 2;

        when(bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED))
                .thenReturn(Flux.fromIterable(sampleBookings));

        when(packageService.getAllPackages(null))
                .thenReturn(Flux.empty());

        when(reviewService.getReviewsByPackage(anyString()))
                .thenReturn(Flux.empty());

        when(reportFormatter.formatMonthlyBookingReportPDF(eq(year), eq(month), any(), any(), any(), any()))
                .thenReturn(Map.of("data", "formattedData"));

        when(pdfGenerator.generateMonthlyBookingPDF(any()))
                .thenReturn(Mono.just(new ByteArrayResource("mock pdf data".getBytes())));

        StepVerifier.create(reportService.generateMonthlyBookingReportPDF(year, month))
                .expectNextMatches(resource -> resource.contentLength() > 0)
                .verifyComplete();

        verify(bookingService).getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED);
        verify(packageService).getAllPackages(null);
        verify(pdfGenerator).generateMonthlyBookingPDF(any());
    }


    @Test
    void whenGenerateMonthlyBookingReportCSV_thenReturnCSV() {
        int year = 2024;
        int month = 2;

        when(bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED))
                .thenReturn(Flux.fromIterable(sampleBookings));

        when(packageService.getAllPackages(null))
                .thenReturn(Flux.empty());  // Ensure Flux.empty() instead of null

        when(reviewService.getReviewsByPackage(anyString()))
                .thenReturn(Flux.empty());

        when(reportFormatter.formatMonthlyBookingReportCSV(eq(year), eq(month), any(), any(), any(), any()))
                .thenReturn(Map.of("data", "formattedData"));

        when(csvGenerator.generateMonthlyBookingCSV(any()))
                .thenReturn(Mono.just(new ByteArrayResource("mock csv data".getBytes())));

        StepVerifier.create(reportService.generateMonthlyBookingReportCSV(year, month))
                .expectNextMatches(resource -> resource.contentLength() > 0)
                .verifyComplete();

        verify(bookingService).getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED);
        verify(packageService).getAllPackages(null);
        verify(csvGenerator).generateMonthlyBookingCSV(any());
    }


    @Test
    void whenNoBookingsFound_thenReturnEmptyPDF() {
        int year = 2024;
        int month = 3;

        when(bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED))
                .thenReturn(Flux.empty());

        when(packageService.getAllPackages(null))
                .thenReturn(Flux.empty());

        when(pdfGenerator.generateMonthlyBookingPDF(any()))
                .thenReturn(Mono.just(new ByteArrayResource("".getBytes())));

        StepVerifier.create(reportService.generateMonthlyBookingReportPDF(year, month))
                .expectNextMatches(resource -> resource.contentLength() == 0)
                .verifyComplete();

        verify(bookingService).getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED);
        verify(packageService).getAllPackages(null);
        verify(pdfGenerator).generateMonthlyBookingPDF(any());
    }

    @Test
    void whenNoBookingsFound_thenReturnEmptyCSV() {
        int year = 2024;
        int month = 3;

        when(bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED))
                .thenReturn(Flux.empty());

        when(packageService.getAllPackages(null))
                .thenReturn(Flux.empty());

        when(csvGenerator.generateMonthlyBookingCSV(any()))
                .thenReturn(Mono.just(new ByteArrayResource("".getBytes())));

        StepVerifier.create(reportService.generateMonthlyBookingReportCSV(year, month))
                .expectNextMatches(resource -> resource.contentLength() == 0)
                .verifyComplete();

        verify(bookingService).getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED);
        verify(packageService).getAllPackages(null);
        verify(csvGenerator).generateMonthlyBookingCSV(any());
    }

}