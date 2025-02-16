package com.traveltrove.betraveltrove.utils.reports;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

@Component
public class CSVGenerator {

    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ByteArrayInputStream generateRevenueCSV(List<Payment> paymentList, String periodType) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            // Write CSV headers
            writer.println("Payment ID,Amount (USD),Currency,Status,Payment Date");

            AtomicLong totalAmount = new AtomicLong(0);
            paymentList.forEach(payment -> {
                writer.println(String.format("%s,%d,%s,%s,%s            ",
                        payment.getPaymentId(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getStatus(),
                        payment.getPaymentDate().format(CSV_DATE_FORMATTER)
                ));
                totalAmount.addAndGet(payment.getAmount());
            });

            writer.println();
            writer.println("Summary,,,,");

            writer.println(String.format("TOTAL,%d,usd,,", totalAmount.get()));
            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public Mono<ByteArrayResource> generateBookingCSV(Map<String, Object> reportData) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            writer.println(reportData.get("title"));
            writer.println("Total Confirmed Bookings," + reportData.get("totalBookings"));

            Map<String, Object> packageStats = (Map<String, Object>) reportData.get("packageStats");
            writer.println("Package,Bookings,Reviews,Average Rating");
            for (Map.Entry<String, Object> entry : packageStats.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().toString());
            }

            writer.flush();
            return Mono.just(new ByteArrayResource(outputStream.toByteArray()));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error generating CSV report", e));
        }
    }
}
