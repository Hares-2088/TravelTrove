package com.traveltrove.betraveltrove.dataaccess.engagement;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
@Data
@Builder
@Document(collection = "package_views")
public class PackageView {
    @Id
    private String id;
    private String userId;
    private String packageId;
    private Instant viewedAt;
}
