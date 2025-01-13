package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.booking.Booking;
import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

@Component
public class MockServerConfigBookingService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1083); // Port 1083 for bookings
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/bookings/{bookingId}
    public void registerGetBookingByIdEndpoint(String bookingId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/bookings/" + bookingId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "bookingId": "%s",
                        "userId": "user1",
                        "packageId": "pack1",
                        "totalPrice": 1200.00,
                        "status": "BOOKING_CONFIRMED",
                        "bookingDate": "2025-04-04"
                    }
                    """, bookingId)));
    }

    // Register GET /api/v1/bookings
    public void registerGetAllBookingsEndpoint(Booking... bookings) {
        StringBuilder bookingsJson = new StringBuilder("[");
        for (Booking booking : bookings) {
            bookingsJson.append(String.format("""
            {
                "bookingId": "%s",
                "userId": "%s",
                "packageId": "%s",
                "totalPrice": %.2f,
                "status": "%s",
                "bookingDate": "%s"
            },
            """,
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getPackageId(),
                    booking.getTotalPrice(),
                    booking.getStatus(),
                    booking.getBookingDate().toString()));
        }
        bookingsJson.setLength(bookingsJson.length() - 1); // Remove last comma
        bookingsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/bookings"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(bookingsJson.toString()));
    }

    // Register GET /api/v1/bookings?status={status}
    public void registerGetBookingsByStatusEndpoint(String status, Booking... bookings) {
        StringBuilder bookingsJson = new StringBuilder("[");
        for (Booking booking : bookings) {
            bookingsJson.append(String.format("""
            {
                "bookingId": "%s",
                "userId": "%s",
                "packageId": "%s",
                "totalPrice": %.2f,
                "status": "%s",
                "bookingDate": "%s"
            },
            """,
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getPackageId(),
                    booking.getTotalPrice(),
                    booking.getStatus(),
                    booking.getBookingDate().toString()));
        }
        bookingsJson.setLength(bookingsJson.length() - 1); // Remove last comma
        bookingsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/bookings")
                        .withQueryStringParameter("status", status))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(bookingsJson.toString()));
    }

    // Register POST /api/v1/bookings
    public void registerCreateBookingEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/bookings")
                        .withBody(org.mockserver.model.JsonBody.json("""
                        {
                            "userId": "user3",
                            "packageId": "pack3",
                            "totalPrice": 1400.00,
                            "status": "PAYMENT_PENDING",
                            "bookingDate": "2025-06-06"
                        }
                        """)))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "bookingId": "generated-booking-id",
                            "userId": "user3",
                            "packageId": "pack3",
                            "totalPrice": 1400.00,
                            "status": "PAYMENT_PENDING",
                            "bookingDate": "2025-06-06"
                        }
                        """));
    }


    // Register PATCH /api/v1/bookings/{bookingId}/confirm
    public void registerConfirmBookingEndpoint(String bookingId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/bookings/" + bookingId + "/confirm"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "bookingId": "%s",
                        "userId": "user1",
                        "packageId": "pack1",
                        "totalPrice": 1200.00,
                        "status": "BOOKING_CONFIRMED",
                        "bookingDate": "2025-04-04"
                    }
                    """, bookingId)));
    }

    // Register DELETE /api/v1/bookings/{bookingId}
    public void registerDeleteBookingEndpoint(String bookingId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/bookings/" + bookingId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }
}