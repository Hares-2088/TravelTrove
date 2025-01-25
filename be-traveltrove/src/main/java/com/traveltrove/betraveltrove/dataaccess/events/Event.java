package com.traveltrove.betraveltrove.dataaccess.events;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "events")
public class Event {

    @Id
    private String id; //private

    private String eventId; //public

    @Nullable
    private String cityId;

    @Nullable
    private String countryId;

    private String name;
    private String description;
    private String eventImageUrl;

}
