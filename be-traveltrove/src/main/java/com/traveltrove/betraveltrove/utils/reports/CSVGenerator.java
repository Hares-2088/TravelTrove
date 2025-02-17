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

    public Mono<ByteArrayResource> generateMonthlyBookingCSV(Map<String, Object> reportData) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            String title = reportData.getOrDefault("title", "Booking Report").toString();
            writer.println(title);

            Object totalBookings = reportData.get("totalBookings");
            writer.println("Total Confirmed Bookings," + (totalBookings != null ? totalBookings.toString() : "0"));

            Object packageStatsObj = reportData.get("packageStats");
            if (packageStatsObj instanceof Map) {
                Map<String, Map<String, Object>> packageStats = (Map<String, Map<String, Object>>) packageStatsObj;
                writer.println("Package,Bookings,Reviews,Average Rating");

                for (Map.Entry<String, Map<String, Object>> entry : packageStats.entrySet()) {
                    String packageName = entry.getKey();
                    Map<String, Object> packageDetails = entry.getValue();

                    int bookings = packageDetails.getOrDefault("bookings", 0) instanceof Integer ?
                            (Integer) packageDetails.get("bookings") : 0;
                    int reviews = packageDetails.getOrDefault("reviews", 0) instanceof Integer ?
                            (Integer) packageDetails.get("reviews") : 0;
                    double avgRating = packageDetails.getOrDefault("averageRating", 0.0) instanceof Double ?
                            (Double) packageDetails.get("averageRating") : 0.0;

                    writer.println(packageName + "," + bookings + "," + reviews + "," + avgRating);
                }
            } else {
                writer.println("No package statistics available.");
            }

            writer.flush();
            return Mono.just(new ByteArrayResource(outputStream.toByteArray()));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error generating CSV report", e));
        }
    }
}
