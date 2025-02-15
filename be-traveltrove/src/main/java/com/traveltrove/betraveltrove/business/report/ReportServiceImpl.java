package com.traveltrove.betraveltrove.business.report;

import com.traveltrove.betraveltrove.business.booking.BookingService;
import com.traveltrove.betraveltrove.business.payment.PaymentService;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.utils.reports.CSVGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final CSVGenerator csvGenerator;


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
}
