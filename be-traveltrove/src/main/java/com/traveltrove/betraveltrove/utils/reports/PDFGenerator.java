package com.traveltrove.betraveltrove.utils.reports;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PDFGenerator {

    public Mono<ByteArrayResource> generateMonthlyBookingPDF(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String htmlContent = (String) reportData.get("htmlContent");
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            return Mono.just(new ByteArrayResource(outputStream.toByteArray()));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error generating PDF report", e));
        }
    }
}