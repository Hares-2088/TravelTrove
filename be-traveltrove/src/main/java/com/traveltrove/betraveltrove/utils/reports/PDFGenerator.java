package com.traveltrove.betraveltrove.utils.reports;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PDFGenerator {

    public Mono<ByteArrayResource> generatePDF(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDoc);

            document.add(new Paragraph(reportData.get("title").toString()));
            document.add(new Paragraph("Total Confirmed Bookings: " + reportData.get("totalBookings")));

            Map<String, Object> packageStats = (Map<String, Object>) reportData.get("packageStats");
            for (Map.Entry<String, Object> entry : packageStats.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue().toString()));
            }

            document.close();
            return Mono.just(new ByteArrayResource(outputStream.toByteArray()));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error generating PDF report", e));
        }
    }
}
