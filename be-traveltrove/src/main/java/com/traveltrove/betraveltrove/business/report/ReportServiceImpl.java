package com.traveltrove.betraveltrove.business.report;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.business.review.ReviewService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.utils.reports.CSVGenerator;
import com.traveltrove.betraveltrove.utils.reports.PDFGenerator;
import com.traveltrove.betraveltrove.utils.reports.ReportFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final PackageService packageService;
    private final PDFGenerator pdfGenerator;
    private final CSVGenerator csvGenerator;
    private final ReportFormatter reportFormatter;

    @Override
    public Mono<ByteArrayInputStream> generatePaymentRevenueReport(String periodType, int year, Integer month) {
        return paymentService.getPaymentsByPeriod(year, month)
                .collectList()
                .map(payments -> {
                    try {
                        return csvGenerator.generateRevenueCSV(payments, periodType);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Mono<ByteArrayResource> generateMonthlyBookingReportPDF(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED)
                .filter(booking -> isBookingInMonth(booking, startDate, endDate))
                .collectList()
                .flatMap(bookings -> generateReport(bookings, year, month))
                .flatMap(pdfGenerator::generatePDF);
    }

    @Override
    public Mono<ByteArrayResource> generateMonthlyBookingReportCSV(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED)
                .filter(booking -> isBookingInMonth(booking, startDate, endDate))
                .collectList()
                .flatMap(bookings -> generateReport(bookings, year, month))
                .flatMap(csvGenerator::generateCSV);
    }

    private boolean isBookingInMonth(BookingResponseModel booking, LocalDate startDate, LocalDate endDate) {
        return !booking.getBookingDate().isBefore(startDate) && !booking.getBookingDate().isAfter(endDate);
    }

    private Mono<Map<String, Object>> generateReport(List<BookingResponseModel> bookings, int year, int month) {
        Map<String, Long> packageBookingCount = bookings.stream()
                .collect(Collectors.groupingBy(BookingResponseModel::getPackageId, Collectors.counting()));

        return packageService.getAllPackages(null)
                .collectList()
                .flatMap(packages -> {
                    List<String> packageIds = packages.stream()
                            .map(PackageResponseModel::getPackageId)
                            .collect(Collectors.toList());

                    // Fetch reviews for each package separately and merge results
                    return Flux.fromIterable(packageIds)
                            .flatMap(reviewService::getReviewsByPackage)
                            .collectList()
                            .map(reviews -> reportFormatter.formatReport(year, month, bookings, packageBookingCount, packages, reviews));
                });
    }
}
