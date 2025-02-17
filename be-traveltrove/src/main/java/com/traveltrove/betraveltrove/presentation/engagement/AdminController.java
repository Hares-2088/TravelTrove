package com.traveltrove.betraveltrove.presentation.engagement;
import com.traveltrove.betraveltrove.business.engagement.EngagementAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
@RestController
@RequestMapping("/api/v1/analytics")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final EngagementAnalyticsService engagementAnalyticsService;

    @GetMapping(value = "/engagement", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EngagementAnalyticsResponseModel> getEngagementAnalytics() {
        log.info("Fetching engagement analytics...");
        return engagementAnalyticsService.getEngagementAnalytics();
    }
}