package com.traveltrove.betraveltrove.presentation.review;

import com.traveltrove.betraveltrove.business.review.ReviewService;
import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.dataaccess.review.ReviewRepository;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    private final String PACKAGE_ID = "package-1";
    private final String INVALID_PACKAGE_ID = "invalid-package-id";

    private final Review review1 = Review.builder()
            .id(UUID.randomUUID().toString())
            .reviewerName("John Doe")
            .review("Excellent!")
            .packageId(PACKAGE_ID)
            .rating(5)
            .build();

    private final Review review2 = Review.builder()
            .id(UUID.randomUUID().toString())
            .reviewerName("Jane Doe")
            .review("Average experience.")
            .packageId(PACKAGE_ID)
            .rating(3)
            .build();

    @BeforeEach
    public void setupDB() {
        Publisher<Review> setupDB = reviewRepository.deleteAll()
                .thenMany(Flux.just(review1, review2))
                .flatMap(reviewRepository::save);

        StepVerifier.create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    //@Test
    void whenAddReview_thenReturnCreatedReview() {
        ReviewRequestModel newReview = new ReviewRequestModel();

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
                .uri("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newReview)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReviewResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(newReview.getReviewerName(), response.getReviewerName());
                    assertEquals(newReview.getReview(), response.getReview());
                    assertEquals(newReview.getRating(), response.getRating());
                });
    }

    //@Test
    void whenGetReviewsByPackage_thenReturnReviews() {
        webTestClient.get()
                .uri("/api/v1/reviews/" + PACKAGE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBodyList(ReviewResponseModel.class)
                .hasSize(2)
                .value(reviews -> {
                    assertEquals(2, reviews.size());
                    assertEquals(review1.getReviewerName(), reviews.get(0).getReviewerName());
                    assertEquals(review2.getReviewerName(), reviews.get(1).getReviewerName());
                });
    }

   //@Test
    void whenGetReviewsByInvalidPackage_thenReturnEmpty() {
        webTestClient.get()
                .uri("/api/v1/reviews/" + INVALID_PACKAGE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReviewResponseModel.class)
                .hasSize(0);
    }

    //@Test
    void whenGetAverageRating_thenReturnAverage() {
        webTestClient.get()
                .uri("/api/v1/reviews/" + PACKAGE_ID + "/average-rating")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReviewResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(4.0, response.getAverageRating()); // (5 + 3) / 2 = 4.0
                });
    }

    //@Test
    void whenGetAverageRating_withInvalidPackage_thenReturnNotFound() {
        webTestClient.get()
                .uri("/api/v1/reviews/" + INVALID_PACKAGE_ID + "/average-rating")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
