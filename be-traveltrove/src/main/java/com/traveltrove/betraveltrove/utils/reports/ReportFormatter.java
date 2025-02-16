package com.traveltrove.betraveltrove.utils.reports;

import com.traveltrove.betraveltrove.presentation.booking.BookingResponseModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        reportData.put("title", "Booking Report for " + month + "/" + year);
        reportData.put("totalBookings", bookings.size());

        Map<String, Object> packageStats = new HashMap<>();
        for (PackageResponseModel tourPackage : packages) {
            String packageId = tourPackage.getPackageId();
            long bookingCount = packageBookingCount.getOrDefault(packageId, 0L);

            List<ReviewResponseModel> packageReviews = reviews.stream()
                    .filter(review -> review.getPackageId().equals(packageId))
                    .collect(Collectors.toList());

            double avgRating = packageReviews.stream()
                    .mapToInt(ReviewResponseModel::getRating)
                    .average()
                    .orElse(0.0);

            packageStats.put(tourPackage.getName(), Map.of(
                    "bookings", bookingCount,
                    "reviews", packageReviews.size(),
                    "averageRating", packageReviews.isEmpty() ? "No Reviews" : avgRating
            ));
        }

        reportData.put("packageStats", packageStats);
        return reportData;
    }
}
