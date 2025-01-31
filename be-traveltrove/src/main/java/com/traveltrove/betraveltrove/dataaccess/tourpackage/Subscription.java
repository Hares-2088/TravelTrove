package com.traveltrove.betraveltrove.dataaccess.tourpackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String id;
    private String userId;
    private String packageId;
    private LocalDateTime subscribedAt;
}
