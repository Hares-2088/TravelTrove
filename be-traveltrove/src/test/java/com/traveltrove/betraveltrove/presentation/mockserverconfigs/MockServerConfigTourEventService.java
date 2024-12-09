package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.tour.TourEvent;
import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

@Component
public class MockServerConfigTourEventService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1083);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/tour-events/{tourEventId}
    public void registerGetTourEventByIdEndpoint(String tourEventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/tour-events/" + tourEventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
                    }
                    """, tourEventId)));
    }

    // Register GET /api/v1/tour-events
    public void registerGetAllTourEventsEndpoint(TourEvent... tourEvents) {
        StringBuilder eventsJson = new StringBuilder("[");
        for (TourEvent event : tourEvents) {
            eventsJson.append(String.format("""
            {
                "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
            },
            """,
                    event.getTourEventId(),
                    event.getEventId(),
                    event.getTourId(),
                    event.getSeq(),
                    event.getSeqDesc()));

        }
        eventsJson.setLength(eventsJson.length() - 1); // Remove last comma
        eventsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/tour-events"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(eventsJson.toString()));
    }

    // Register POST /api/v1/tour-events
    public void registerAddTourEventEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/tour-events")
                        .withBody("""
                    {
                        "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
                    }
                    """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                    {
                        "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
                    }
                    """));
    }

    // Register DELETE /api/v1/tour-events/{tourEventId}
    public void registerDeleteTourEventEndpoint(String tourEventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/tour-events/" + tourEventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }

    // Register PUT /api/v1/tour-events/{tourEventId}
    public void registerUpdateTourEventEndpoint(String tourEventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/tour-events/" + tourEventId)
                        .withBody(String.format("""
                    {
                        "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
                    }
                    """)))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "tourEventId": "%s",
                        "tourId": "1",
                        "seq": 1,
                        "seqDesc": "First Event",
                        "eventId": "1"
                    }
                    """, tourEventId)));
    }

    // Register GET /api/v1/tour-events/{tourEventId} with invalid tourEventId
    public void registerGetTourEventByInvalidIdEndpoint(String tourEventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/tour-events/" + tourEventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody(String.format("""
                    {
                        "message": "Tour event id not found: %s"
                    }
                    """, tourEventId)));
    }
}
