package com.traveltrove.betraveltrove.business.report;

import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

public interface ReportService {

    Mono<ByteArrayInputStream> generatePaymentRevenueReport(String periodType, int year, Integer month);
}
