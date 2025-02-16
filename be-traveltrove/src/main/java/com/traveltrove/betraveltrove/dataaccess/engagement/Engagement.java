package com.traveltrove.betraveltrove.dataaccess.engagement;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "engagements")
public class Engagement {

    @Id
    private String id;

    private String packageId;   // The package being engaged with
    private String userId;      // Optional: Can be null for anonymous users

    private int viewCount;
    private int wishlistCount;
    private int shareCount;

    private Instant lastUpdated; // Timestamp of last interaction
}
