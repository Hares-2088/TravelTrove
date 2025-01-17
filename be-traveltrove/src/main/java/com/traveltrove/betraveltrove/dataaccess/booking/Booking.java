package com.traveltrove.betraveltrove.dataaccess.booking;

import com.traveltrove.betraveltrove.dataaccess.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "booking")
@Data
@NoArgsConstructor
public class Booking {

    @Id
    private String id; // private DB ID

    private String bookingId; // public ID
    private String userId;
    private String packageId;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDate bookingDate;

    // We do NOT rely solely on field initialization here;
    // we'll handle it in the builder constructor below.
    private List<String> travelerIds;

    /**
     * Custom builder constructor that ensures travelerIds is never null.
     */
    @Builder
    public Booking(
            String id,
            String bookingId,
            String userId,
            String packageId,
            Double totalPrice,
            BookingStatus status,
            LocalDate bookingDate,
            List<String> travelerIds // the builder field
    ) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bookingDate = bookingDate;

        // Force a non-null list
        this.travelerIds = (travelerIds == null) ? new ArrayList<>() : travelerIds;
    }
}
