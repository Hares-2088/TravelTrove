package com.traveltrove.betraveltrove.utils.entitymodelyutils;

import com.traveltrove.betraveltrove.dataaccess.review.Review;
import com.traveltrove.betraveltrove.presentation.review.ReviewRequestModel;
import com.traveltrove.betraveltrove.presentation.review.ReviewResponseModel;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;


public class ReviewEntityModelUtil {
    public static ReviewResponseModel toReviewResponseModel(Review review) {
        ReviewResponseModel reviewResponseModel = new ReviewResponseModel();

        reviewResponseModel.setReviewId(generateUUIDString());
        BeanUtils.copyProperties(review, reviewResponseModel);

        return reviewResponseModel;
    }

    public static Review toReviewEntity(ReviewRequestModel reviewRequestModel) {
        return Review.builder()
                .packageId(reviewRequestModel.getPackageId())
                .userId(reviewRequestModel.getUserId())
                .reviewerName(reviewRequestModel.getReviewerName())
                .rating(reviewRequestModel.getRating())
                .review(reviewRequestModel.getReview())
                .date(LocalDateTime.now())
                .build();
    }

    private static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
