package com.traveltrove.betraveltrove.utils.reports;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class CSVGenerator {

    public ByteArrayInputStream generateRevenueCSV(List<Payment> paymentList, String periodType) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            // Write CSV headers
            writer.println("Payment ID, Amount, Currency, Status, Payment Date");

            // Write CSV data
            paymentList.forEach(payment -> writer.println(String.format("%s,%d,%s,%s,%s",
                    payment.getPaymentId(),
                    payment.getAmount(), // Keep in cents or convert to dollars if needed
                    payment.getCurrency(),
                    payment.getStatus(),
                    payment.getPaymentDate())));

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
