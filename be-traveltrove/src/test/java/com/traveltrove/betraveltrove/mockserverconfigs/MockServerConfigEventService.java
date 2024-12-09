package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;
import com.traveltrove.betraveltrove.dataaccess.events.Event;

@Component
public class MockServerConfigEventService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1082);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/events/{eventId}
    public void registerGetEventByIdEndpoint(String eventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/events/" + eventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "eventId": "%s",
                        "name": "Event 1",
                        "description": "Description 1",
                        "cityId": "1",
                        "countryId": "1",
                        "image": "image1.jpg"
                    }
                    """, eventId)));
    }

    // Register GET /api/v1/events
    public void registerGetAllEventsEndpoint(Event... events) {
        StringBuilder eventsJson = new StringBuilder("[");
        for (Event event : events) {
            eventsJson.append(String.format("""
            {
                "eventId": "%s",
                "name": "%s",
                "description": "%s",
                "cityId": "%s",
                "countryId": "%s",
                "image": "%s"
            },
            """,
                    event.getEventId(),
                    event.getName(),
                    event.getDescription(),
                    event.getCityId(),
                    event.getCountryId(),
                    event.getImage()));
        }
        eventsJson.setLength(eventsJson.length() - 1); // Remove last comma
        eventsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/events"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(eventsJson.toString()));
    }



    // Register GET /api/v1/events?cityId={cityId}
    public void registerGetEventsByCityIdEndpoint(String cityId, Event... events) {
        StringBuilder eventsJson = new StringBuilder("[");
        for (Event event : events) {
            eventsJson.append(String.format("""
            {
                "eventId": "%s",
                "name": "%s",
                "description": "%s",
                "cityId": "%s",
                "countryId": "%s",
                "image": "%s"
            },
            """,
                    event.getEventId(),
                    event.getName(),
                    event.getDescription(),
                    event.getCityId(),
                    event.getCountryId(),
                    event.getImage()));
        }
        eventsJson.setLength(eventsJson.length() - 1); // Remove last comma
        eventsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/events")
                        .withQueryStringParameter("cityId", cityId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(eventsJson.toString()));
    }

    // Register GET /api/v1/events?countryId={countryId}
    public void registerGetEventsByCountryIdEndpoint(String countryId, Event... events) {
        StringBuilder eventsJson = new StringBuilder("[");
        for (Event event : events) {
            eventsJson.append(String.format("""
            {
                "eventId": "%s",
                "name": "%s",
                "description": "%s",
                "cityId": "%s",
                "countryId": "%s",
                "image": "%s"
            },
            """,
                    event.getEventId(),
                    event.getName(),
                    event.getDescription(),
                    event.getCityId(),
                    event.getCountryId(),
                    event.getImage()));
        }
        eventsJson.setLength(eventsJson.length() - 1); // Remove last comma
        eventsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/events")
                        .withQueryStringParameter("countryId", countryId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(eventsJson.toString()));
    }

    // Register POST /api/v1/events
    public void registerAddEventEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/events")
                        .withBody("""
                    {
                        "name": "Event 3",
                        "description": "Description 3",
                        "cityId": "3",
                        "countryId": "3",
                        "image": "image3.jpg"
                    }
                    """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                    {
                        "eventId": "3",
                        "name": "Event 3",
                        "description": "Description 3",
                        "cityId": "3",
                        "countryId": "3",
                        "image": "image3.jpg"
                    }
                    """));
    }

    // Register PUT /api/v1/events/{eventId}
    public void registerUpdateEventEndpoint(String eventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/events/" + eventId)
                        .withBody(String.format("""
                    {
                        "name": "Updated Event",
                        "description": "Updated Description",
                        "cityId": "3",
                        "countryId": "3",
                        "image": "updated_image.jpg"
                    }
                    """)))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "eventId": "%s",
                        "name": "Updated Event",
                        "description": "Updated Description",
                        "cityId": "3",
                        "countryId": "3",
                        "image": "updated_image.jpg"
                    }
                    """, eventId)));
    }

    // Register DELETE /api/v1/events/{eventId}
    public void registerDeleteEventEndpoint(String eventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/events/" + eventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }

    //Register GET /api/v1/events/{eventId} with invalid event id
    public void registerGetEventByInvalidIdEndpoint(String eventId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/events/" + eventId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                    {
                        "message": "Event id not found: " + eventId
                    }
                    """));
    }
}