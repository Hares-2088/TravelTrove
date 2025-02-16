package com.traveltrove.betraveltrove.presentation.report;

import com.traveltrove.betraveltrove.business.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;


    @GetMapping("/revenue")
    public Mono<ResponseEntity<InputStreamResource>> downloadRevenueReport(
            @RequestParam String periodType, // monthly or yearly
            @RequestParam int year,
            @RequestParam(required = false) Integer month // 1-12 (optional for yearly)
    ) {
        return reportService.generatePaymentRevenueReport(periodType, year, month)
                .map(report -> {
                    String fileName = generateFileName(periodType, year, month);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(new InputStreamResource(report));
                });
    }

    private String generateFileName(String periodType, int year, Integer month) {
        if ("monthly".equalsIgnoreCase(periodType) && month != null) {
            return String.format("revenue-report-%d-%02d.csv", year, month);
        } else {
            return String.format("revenue-report-%d.csv", year);
        }
    }
}
