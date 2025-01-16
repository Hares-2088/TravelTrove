package com.traveltrove.betraveltrove.presentation.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestModel {
    private String packageId;
    private String userId;
    private String reviewerName;
    private Integer rating;
    private String review;
    private LocalDateTime date;
}
