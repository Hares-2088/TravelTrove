package com.traveltrove.betraveltrove.utils.reports;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
                writer.println(String.format("%s,%d,%s,%s,%s",
                        payment.getPaymentId(),
                        payment.getAmount() ,
                        payment.getCurrency(),
                        payment.getStatus(),
                        payment.getPaymentDate().format(CSV_DATE_FORMATTER)
                ));
                totalAmount.addAndGet(payment.getAmount());
            });

            writer.println();
            writer.println("Summary,,,,");

            writer.println(String.format("TOTAL,%d,USD,,", totalAmount.get()));
            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
