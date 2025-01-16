package com.traveltrove.betraveltrove.business.review;

import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.dataaccess.review.ReviewRepository;
import com.traveltrove.betraveltrove.presentation.review.ReviewRequestModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.ReviewEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Mono<ReviewResponseModel> addReview(Mono<ReviewRequestModel> reviewRequestModel) {
        return reviewRequestModel
                .map(ReviewEntityModelUtil::toReviewEntity) // Pass the entire object
                .flatMap(reviewRepository::save)
                .map(ReviewEntityModelUtil::toReviewResponseModel);
    }


    @Override
    public Flux<ReviewResponseModel> getReviewsByPackage(String packageId) {
        return reviewRepository.findByPackageId(packageId)
                .map(ReviewEntityModelUtil::toReviewResponseModel)
                .switchIfEmpty(Flux.error(new NotFoundException("No reviews found for package: " + packageId)))
                .doOnComplete(() -> log.info("Fetched reviews for packageId: {}", packageId));
    }

    @Override
    public Mono<ReviewResponseModel> getAverageRating(String packageId) {
        return reviewRepository.findByPackageId(packageId)
                .map(Review::getRating) // Extract ratings
                .collectList() // Collect ratings into a list
                .filter(ratings -> !ratings.isEmpty()) // Ensure list is not empty
                .map(ratings -> ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0)) // Calculate average
                .map(average -> ReviewResponseModel.builder()
                        .packageId(packageId)
                        .averageRating(average)
                        .build()) // Create a response model
                .switchIfEmpty(Mono.error(new NotFoundException("No reviews found for package: " + packageId)))
                .doOnSuccess(avg -> log.info("Calculated average rating for packageId {}: {}", packageId, avg))
                .doOnError(error -> log.error("Error calculating average rating: {}", error.getMessage()));
    }

}
