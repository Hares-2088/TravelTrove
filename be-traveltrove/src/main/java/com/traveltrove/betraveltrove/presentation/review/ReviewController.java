package com.traveltrove.betraveltrove.presentation.review;


import com.traveltrove.betraveltrove.business.review.ReviewService;
import com.traveltrove.betraveltrove.dataaccess.review.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reviews")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Mono<ResponseEntity<ReviewResponseModel>> addReview(
            @RequestBody Mono<ReviewRequestModel> reviewRequestModel) {
        return reviewService.addReview(reviewRequestModel)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{packageId}")
    public Flux<ReviewResponseModel> getReviewsByPackage(@PathVariable String packageId) {
        return reviewService.getReviewsByPackage(packageId);
    }

    @GetMapping("/{packageId}/average-rating")
    public Mono<ResponseEntity<ReviewResponseModel>> getAverageRating(@PathVariable String packageId) {
        return reviewService.getAverageRating(packageId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
