package com.traveltrove.betraveltrove.utils.reports;

import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportFormatter {

    public Map<String, Object> formatReport(
            int year,
            int month,
            List<BookingResponseModel> bookings,
            Map<String, Long> packageBookingCount,
            List<PackageResponseModel> packages,
            List<ReviewResponseModel> reviews
    ) {
        Map<String, Object> reportData = new HashMap<>();

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h1>Booking Report for ").append(month).append("/").append(year).append("</h1>");
        htmlContent.append("<p>Total Confirmed Bookings: ").append(bookings.size()).append("</p>");
        htmlContent.append("<table border='1'><tr><th>Package</th><th>Bookings</th><th>Reviews</th><th>Average Rating</th></tr>");

        for (PackageResponseModel tourPackage : packages) {
            String packageId = tourPackage.getPackageId();
            long bookingCount = packageBookingCount.getOrDefault(packageId, 0L);

            List<ReviewResponseModel> packageReviews = reviews.stream()
                    .filter(review -> review.getPackageId().equals(packageId))
                    .toList();

            double avgRating = packageReviews.stream()
                    .mapToInt(ReviewResponseModel::getRating)
                    .average()
                    .orElse(0.0);

            htmlContent.append("<tr>")
                    .append("<td>").append(tourPackage.getName()).append("</td>")
                    .append("<td>").append(bookingCount).append("</td>")
                    .append("<td>").append(packageReviews.size()).append("</td>")
                    .append("<td>").append(packageReviews.isEmpty() ? "No Reviews" : String.format("%.2f", avgRating)).append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");
        htmlContent.append("</body></html>");

        reportData.put("htmlContent", htmlContent.toString());
        return reportData;
    }
}