package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;
import com.traveltrove.betraveltrove.dataaccess.traveler.Traveler;

@Component
public class MockServerConfigTravelerService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1083); // Unique port for Traveler mock server
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/travelers
    public void registerGetAllTravelersEndpoint(Traveler... travelers) {
        StringBuilder travelersJson = new StringBuilder("[");
        for (Traveler traveler : travelers) {
            travelersJson.append(String.format("""
            {
                "travelerId": "%s",
                "seq": %d,
                "firstName": "%s",
                "lastName": "%s",
                "addressLine1": "%s",
                "addressLine2": "%s",
                "city": "%s",
                "state": "%s",
                "email": "%s",
                "countryId": "%s"
            },
            """,
                    traveler.getTravelerId(),
                    traveler.getSeq(),
                    traveler.getFirstName(),
                    traveler.getLastName(),
                    traveler.getAddressLine1(),
                    traveler.getAddressLine2(),
                    traveler.getCity(),
                    traveler.getState(),
                    traveler.getEmail(),
                    traveler.getCountryId()));
        }
        travelersJson.setLength(travelersJson.length() - 1); // Remove last comma
        travelersJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/travelers"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(travelersJson.toString()));
    }

    // Register GET /api/v1/travelers with firstName query parameter
    public void registerGetTravelersByFirstNameEndpoint(String firstName, Traveler... travelers) {
        StringBuilder travelersJson = new StringBuilder("[");
        for (Traveler traveler : travelers) {
            travelersJson.append(String.format("""
            {
                "travelerId": "%s",
                "seq": %d,
                "firstName": "%s",
                "lastName": "%s",
                "addressLine1": "%s",
                "addressLine2": "%s",
                "city": "%s",
                "state": "%s",
                "email": "%s",
                "countryId": "%s"
            },
            """,
                    traveler.getTravelerId(),
                    traveler.getSeq(),
                    traveler.getFirstName(),
                    traveler.getLastName(),
                    traveler.getAddressLine1(),
                    traveler.getAddressLine2(),
                    traveler.getCity(),
                    traveler.getState(),
                    traveler.getEmail(),
                    traveler.getCountryId()));
        }
        travelersJson.setLength(travelersJson.length() - 1); // Remove last comma
        travelersJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/travelers")
                        .withQueryStringParameter("firstName", firstName))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(travelersJson.toString()));
    }

    // Register GET /api/v1/travelers/{travelerId}
    public void registerGetTravelerByIdEndpoint(String travelerId, Traveler traveler) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/travelers/" + travelerId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "travelerId": "%s",
                        "seq": %d,
                        "firstName": "%s",
                        "lastName": "%s",
                        "addressLine1": "%s",
                        "addressLine2": "%s",
                        "city": "%s",
                        "state": "%s",
                        "email": "%s",
                        "countryId": "%s"
                    }
                    """,
                                traveler.getTravelerId(),
                                traveler.getSeq(),
                                traveler.getFirstName(),
                                traveler.getLastName(),
                                traveler.getAddressLine1(),
                                traveler.getAddressLine2(),
                                traveler.getCity(),
                                traveler.getState(),
                                traveler.getEmail(),
                                traveler.getCountryId())));
    }

    // Register POST /api/v1/travelers
    public void registerCreateTravelerEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/travelers")
                        .withBody("""
                    {
                        "seq": 1,
                        "firstName": "John",
                        "lastName": "Doe",
                        "addressLine1": "123 Main St",
                        "addressLine2": "Apt 4B",
                        "city": "Cityville",
                        "state": "Stateville",
                        "email": "johndoe@example.com",
                        "countryId": "1"
                    }
                    """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                    {
                        "travelerId": "1",
                        "seq": 1,
                        "firstName": "John",
                        "lastName": "Doe",
                        "addressLine1": "123 Main St",
                        "addressLine2": "Apt 4B",
                        "city": "Cityville",
                        "state": "Stateville",
                        "email": "johndoe@example.com",
                        "countryId": "1"
                    }
                    """));
    }

    // Register PUT /api/v1/travelers/{travelerId}
    public void registerUpdateTravelerEndpoint(String travelerId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/travelers/" + travelerId)
                        .withBody(String.format("""
                    {
                        "seq": 2,
                        "firstName": "Updated Name",
                        "lastName": "Updated Last",
                        "addressLine1": "Updated Address",
                        "addressLine2": "Updated Apt",
                        "city": "Updated City",
                        "state": "Updated State",
                        "email": "updatedemail@example.com",
                        "countryId": "2"
                    }
                    """)))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "travelerId": "%s",
                        "seq": 2,
                        "firstName": "Updated Name",
                        "lastName": "Updated Last",
                        "addressLine1": "Updated Address",
                        "addressLine2": "Updated Apt",
                        "city": "Updated City",
                        "state": "Updated State",
                        "email": "updatedemail@example.com",
                        "countryId": "2"
                    }
                    """, travelerId)));
    }

    // Register DELETE /api/v1/travelers/{travelerId}
    public void registerDeleteTravelerEndpoint(String travelerId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/travelers/" + travelerId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }

    // Register GET /api/v1/travelers/{invalidId}
    public void registerGetTravelerByInvalidIdEndpoint(String travelerId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/travelers/" + travelerId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody(String.format("""
                    {
                        "message": "Traveler id not found: %s"
                    }
                    """, travelerId)));
    }
}
