package com.traveltrove.betraveltrove.dataaccess.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "review")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    private String id;

    private String reviewId;
    private String packageId;
    private String userId;
    private String reviewerName;
    private Integer rating;
    private String review;
    private LocalDateTime date;


}
