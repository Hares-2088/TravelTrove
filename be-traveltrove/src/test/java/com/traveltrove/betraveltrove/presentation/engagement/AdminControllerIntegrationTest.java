package com.traveltrove.betraveltrove.presentation.engagement;

import com.traveltrove.betraveltrove.business.engagement.EngagementAnalyticsService;
import com.traveltrove.betraveltrove.dataaccess.engagement.Engagement;
import com.traveltrove.betraveltrove.dataaccess.engagement.EngagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class AdminControllerIntegrationTest {
    @Mock
    private EngagementAnalyticsService engagementAnalyticsService;

    @InjectMocks
    private AdminController adminController;

    private WebTestClient webTestClient;

    private EngagementAnalyticsResponseModel sampleAnalytics1;
    private EngagementAnalyticsResponseModel sampleAnalytics2;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(adminController).build();

        sampleAnalytics1 = EngagementAnalyticsResponseModel.builder()
                .packageId("package1")
                .totalViews(100)
                .totalWishlists(50)
                .totalShares(20)
                .build();

        sampleAnalytics2 = EngagementAnalyticsResponseModel.builder()
                .packageId("package2")
                .totalViews(150)
                .totalWishlists(75)
                .totalShares(30)
                .build();
    }

 /*   @Test
    void testGetEngagementAnalytics() {
        when(engagementAnalyticsService.getEngagementAnalytics())
                .thenReturn(Flux.just(sampleAnalytics1, sampleAnalytics2));

        webTestClient.get()
                .uri("/api/v1/analytics/engagement")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8") // ✅ Match the actual header
                .expectBodyList(EngagementAnalyticsResponseModel.class)
                .hasSize(0);


        verify(engagementAnalyticsService, times(1)).getEngagementAnalytics();
    }*/

    @Test
    void testGetEngagementAnalytics_EmptyResponse() {
        when(engagementAnalyticsService.getEngagementAnalytics())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/analytics/engagement")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8") // ✅ Match the actual header
                .expectBodyList(EngagementAnalyticsResponseModel.class)
                .hasSize(0);


        verify(engagementAnalyticsService, times(1)).getEngagementAnalytics();
    }

}