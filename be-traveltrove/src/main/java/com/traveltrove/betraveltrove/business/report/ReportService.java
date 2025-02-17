package com.traveltrove.betraveltrove.business.report;

import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

public interface ReportService {

    Mono<ByteArrayInputStream> generatePaymentRevenueReport(String periodType, int year, Integer month);
    Mono<ByteArrayResource> generateMonthlyBookingReportPDF(int year, int month);
    Mono<ByteArrayResource> generateMonthlyBookingReportCSV(int year, int month);
}
