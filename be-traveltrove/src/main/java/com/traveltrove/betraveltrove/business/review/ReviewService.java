package com.traveltrove.betraveltrove.business.review;

import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.presentation.review.ReviewRequestModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {
    Mono<ReviewResponseModel> addReview(Mono<ReviewRequestModel> reviewRequestModel);
    Flux<ReviewResponseModel> getReviewsByPackage(String packageId);
    Mono<ReviewResponseModel> getAverageRating(String packageId);
}
