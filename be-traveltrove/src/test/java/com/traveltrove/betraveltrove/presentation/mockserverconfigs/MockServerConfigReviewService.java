package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.presentation.review.ReviewRequestModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.stereotype.Component;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@Component
public class MockServerConfigReviewService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = startClientAndServer(1087);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register POST /api/v1/reviews
    public void registerAddReviewEndpoint(ReviewRequestModel reviewRequest, ReviewResponseModel reviewResponse) {
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/reviews")
                        .withBody(String.format("""
                            {
                                "rating": %d
                            }
                            """, reviewRequest.getRating())))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                            {
                                "reviewId": "%s",
                                "rating": %d
                            }
                            """,
                                reviewResponse.getReviewId(),
                                reviewResponse.getRating())));
    }

    // Register GET /api/v1/reviews/{packageId}
    public void registerGetReviewsByPackageEndpoint(String packageId, Review... reviews) {
        StringBuilder reviewsJson = new StringBuilder("[");
        for (Review review : reviews) {
            reviewsJson.append(String.format("""
                {
                    "reviewId": "%s",
                    "rating": %d
                },
                """, review.getReviewId(),review.getRating()));
        }
        reviewsJson.deleteCharAt(reviewsJson.length() - 1); // Remove last comma
        reviewsJson.append("]");

        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/reviews/" + packageId))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(reviewsJson.toString()));
    }

    // Register GET /api/v1/reviews/{packageId}/average-rating
    public void registerGetAverageRatingEndpoint(String packageId, double averageRating) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/reviews/" + packageId + "/average-rating"))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                            {
                                "averageRating": %f
                            }
                            """, averageRating)));
    }

    // Register Invalid GET /api/v1/reviews/{packageId}
    public void registerInvalidGetReviewsEndpoint(String packageId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/reviews/" + packageId))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                            {
                                "message": "Reviews not found for package: %s"
                            }
                            """, packageId)));
    }

    // Register Invalid GET /api/v1/reviews/{packageId}/average-rating
    public void registerInvalidGetAverageRatingEndpoint(String packageId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/reviews/" + packageId + "/average-rating"))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                            {
                                "message": "Average rating not found for package: %s"
                            }
                            """, packageId)));
    }
}
