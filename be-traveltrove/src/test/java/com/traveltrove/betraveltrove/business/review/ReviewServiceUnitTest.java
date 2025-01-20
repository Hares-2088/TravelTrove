package com.traveltrove.betraveltrove.business.review;

import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.dataaccess.review.ReviewRepository;
import com.traveltrove.betraveltrove.presentation.review.ReviewRequestModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;

    @Test
    public void addReview_thenReviewAdded() {
        // Arrange: Set up the input and expected output
        ReviewRequestModel requestModel = new ReviewRequestModel();
        requestModel.setReviewerName("Test Reviewer");
        requestModel.setReview("Great package!");
        requestModel.setRating(5);

        Review review = new Review();
        review.setReviewerName(requestModel.getReviewerName());
        review.setReview(requestModel.getReview());
        review.setRating(requestModel.getRating());

        ReviewResponseModel expectedResponse = new ReviewResponseModel();
        expectedResponse.setReviewerName(review.getReviewerName());
        expectedResponse.setReview(review.getReview());
        expectedResponse.setRating(review.getRating());

        // Mock the repository behavior
        when(reviewRepository.save(any(Review.class))).thenReturn(Mono.just(review));

        // Act & Assert: Use StepVerifier to verify the behavior of addReview()
        StepVerifier.create(reviewServiceImpl.addReview(Mono.just(requestModel)))
                .expectNextMatches(response ->
                        response.getReviewerName().equals(expectedResponse.getReviewerName()) &&
                                response.getReview().equals(expectedResponse.getReview()) &&
                                response.getRating() == expectedResponse.getRating()
                )
                .verifyComplete();

        // Verify that the repository was called with the correct data
        verify(reviewRepository, times(1)).save(any(Review.class));
    }


    @Test
    public void getReviewsByPackage_thenReturnReviews() {
        // Arrange: Create sample reviews and mock the repository
        String packageId = "3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16";

        Review review1 = new Review();
        review1.setPackageId(packageId);
        review1.setReviewerName("John Doe");
        review1.setReview("Great package!");
        review1.setRating(5);

        Review review2 = new Review();
        review2.setPackageId(packageId);
        review2.setReviewerName("Jane Doe");
        review2.setReview("Not bad");
        review2.setRating(4);

        when(reviewRepository.findByPackageId(packageId)).thenReturn(Flux.just(review1, review2));

        // Act & Assert: Verify the service processes the reviews correctly
        StepVerifier.create(reviewServiceImpl.getReviewsByPackage(packageId))
                .expectNextMatches(review ->
                        review.getPackageId().equals(packageId) &&
                                review.getReviewerName().equals("John Doe") &&
                                review.getReview().equals("Great package!") &&
                                review.getRating() == 5
                )
                .expectNextMatches(review ->
                        review.getPackageId().equals(packageId) &&
                                review.getReviewerName().equals("Jane Doe") &&
                                review.getReview().equals("Not bad") &&
                                review.getRating() == 4
                )
                .verifyComplete();

        // Verify the repository was called once with the correct packageId
        verify(reviewRepository, times(1)).findByPackageId(packageId);
    }



    @Test
    public void getAverageRating_thenReturnAverageRating() {
        // Arrange: Set up the input, mock data, and expected output
        String packageId = "3e1a7d9b-5c4f-4a3b-a7f6-cbd4f8e2af16";

        Review review1 = new Review();
        review1.setRating(4);

        Review review2 = new Review();
        review2.setRating(5);

        double expectedAverage = (review1.getRating() + review2.getRating()) / 2.0;

        ReviewResponseModel expectedResponse = ReviewResponseModel.builder()
                .packageId(packageId)
                .averageRating(expectedAverage)
                .build();

        // Mock the repository behavior
        when(reviewRepository.findByPackageId(packageId)).thenReturn(Flux.just(review1, review2));

        // Act & Assert: Use StepVerifier to verify the service logic
        StepVerifier.create(reviewServiceImpl.getAverageRating(packageId))
                .expectNextMatches(response ->
                        response.getPackageId().equals(expectedResponse.getPackageId()) &&
                                response.getAverageRating() == expectedAverage
                )
                .verifyComplete();

        // Verify that the repository was called once with the correct packageId
        verify(reviewRepository, times(1)).findByPackageId(packageId);
    }




}
