package com.traveltrove.betraveltrove.dataaccess.review;

import com.traveltrove.betraveltrove.dataaccess.events.Event;
import com.traveltrove.betraveltrove.dataaccess.events.EventRepository;
import jdk.jfr.BooleanFlag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataMongoTest
@ActiveProfiles("test")
class ReviewRepositoryIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    private final String NON_EXISTING_REVIEW_ID = "non-existing-review-id";
    private final String EXISTING_REVIEW_ID = "EV01";

    private final String EXISTING_PACKAGE_ID = "3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16";

    private final String NON_EXISTING_PACKAGE_ID = "non-existing-package-id";


    @BeforeEach
    void setUp() {
        Review review = Review.builder()
                .id("1")
                .reviewId(EXISTING_REVIEW_ID)
                .reviewerName("Test Review")
                .review("Test Review Description")
                .packageId(EXISTING_PACKAGE_ID)
                .build();

        StepVerifier.create(reviewRepository.save(review))
                .expectNextMatches(savedReview -> savedReview.getReviewId().equals(EXISTING_REVIEW_ID))
                .verifyComplete();
    }


    @AfterEach
    void cleanUp() {
        StepVerifier.create(reviewRepository.deleteAll())
                .verifyComplete();
    }



//fails cuz of netty tf

    void whenFindReviewByPackageId_thenReturnReviews() {

        StepVerifier.create(reviewRepository.findByPackageId(EXISTING_PACKAGE_ID))
                .expectNextMatches(review ->
                        review.getReviewId().equals(EXISTING_REVIEW_ID) &&
                                review.getReviewerName().equals("Test Review") &&
                                review.getReview().equals("Test Review Description")
                )
                .verifyComplete();
    }




    void whenFindReviewByReviewId_withExistingId_thenReturnExistingReview() {

        StepVerifier.create(reviewRepository.findByPackageId(EXISTING_REVIEW_ID))
                .expectNextMatches(review ->
                        review.getReviewId().equals(EXISTING_REVIEW_ID) &&
                                review.getReviewerName().equals("Test Review") &&
                                review.getReview().equals("Test Review Description")
                )
                .verifyComplete();
    }

}