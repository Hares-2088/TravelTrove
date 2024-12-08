package com.traveltrove.betraveltrove.dataaccess.events;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String image;

}
