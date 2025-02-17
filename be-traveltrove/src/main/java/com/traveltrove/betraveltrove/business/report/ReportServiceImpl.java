package com.traveltrove.betraveltrove.business.report;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.business.review.ReviewService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingService bookingService;
    private final PaymentService paymentService;
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
                .flatMap(bookings -> generateReportPDF(bookings, year, month))
                .flatMap(pdfGenerator::generateMonthlyBookingPDF);
    }

    @Override
    public Mono<ByteArrayResource> generateMonthlyBookingReportCSV(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return bookingService.getBookingsByStatus(BookingStatus.BOOKING_CONFIRMED)
                .filter(booking -> isBookingInMonth(booking, startDate, endDate))
                .collectList()
                .flatMap(bookings -> generateReportCSV(bookings, year, month))
                .flatMap(csvGenerator::generateMonthlyBookingCSV);
    }

    private boolean isBookingInMonth(BookingResponseModel booking, LocalDate startDate, LocalDate endDate) {
        return !booking.getBookingDate().isBefore(startDate) && !booking.getBookingDate().isAfter(endDate);
    }

    private Mono<Map<String, Object>> generateReportPDF(List<BookingResponseModel> bookings, int year, int month) {
        Map<String, Long> packageBookingCount = bookings.stream()
                .collect(Collectors.groupingBy(BookingResponseModel::getPackageId, Collectors.counting()));

        List<String> bookedPackageIds = new ArrayList<>(packageBookingCount.keySet());

        return packageService.getAllPackages(null)
                .filter(pkg -> bookedPackageIds.contains(pkg.getPackageId()))
                .collectList()
                .flatMap(packages -> Flux.fromIterable(bookedPackageIds)
                        .flatMap(reviewService::getReviewsByPackage)
                        .collectList()
                        .map(reviews -> reportFormatter.formatMonthlyBookingReportPDF(year, month, bookings, packageBookingCount, packages, reviews)));
    }

    private Mono<Map<String, Object>> generateReportCSV(List<BookingResponseModel> bookings, int year, int month) {
        Map<String, Long> packageBookingCount = bookings.stream()
                .collect(Collectors.groupingBy(BookingResponseModel::getPackageId, Collectors.counting()));

        List<String> bookedPackageIds = new ArrayList<>(packageBookingCount.keySet());

        return packageService.getAllPackages(null)
                .filter(pkg -> bookedPackageIds.contains(pkg.getPackageId()))
                .collectList()
                .flatMap(packages -> Flux.fromIterable(bookedPackageIds)
                        .flatMap(reviewService::getReviewsByPackage)
                        .collectList()
                        .map(reviews -> reportFormatter.formatMonthlyBookingReportCSV(year, month, bookings, packageBookingCount, packages, reviews)));
    }
}
