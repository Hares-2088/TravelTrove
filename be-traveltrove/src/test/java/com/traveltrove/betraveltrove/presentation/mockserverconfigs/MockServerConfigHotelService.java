package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.hotel.Hotel;
import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

@Component
public class MockServerConfigHotelService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1081);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/hotels/{hotelId}
    public void registerGetHotelByIdEndpoint(String hotelId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/hotels/" + hotelId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "hotelId": "%s",
                        "name": "Hotel 1",
                        "description": "Description 1",
                        "cityId": "1",
                        "countryId": "1",
                        "image": "image1.jpg"
                    }
                    """, hotelId)));
    }

    // Register GET /api/v1/hotels
    public void registerGetAllHotelsEndpoint(Hotel... hotels) {
        StringBuilder hotelsJson = new StringBuilder("[");
        for (Hotel hotel : hotels) {
            hotelsJson.append(String.format("""
                            {
                                "hotelId": "%s",
                                "name": "%s",
                                "url": "%s",
                                "cityId": "%s"
                            },
                            """,
                    hotel.getHotelId(),
                    hotel.getName(),
                    hotel.getUrl(),
                    hotel.getCityId()));
        }
        hotelsJson.setLength(hotelsJson.length() - 1);
        hotelsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/hotels"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(hotelsJson.toString()));
    }

    // Register GET /api/v1/hotels?cityId={cityId}
    public void registerGetHotelsByCityIdEndpoint(String cityId, Hotel... hotels) {
        StringBuilder hotelsJson = new StringBuilder("[");
        for (Hotel hotel : hotels) {
            hotelsJson.append(String.format("""
                            {
                                "hotelId": "%s",
                                "name": "%s",
                                "url": "%s",
                                "cityId": "%s"
                            },
                            """,
                    hotel.getHotelId(),
                    hotel.getName(),
                    hotel.getUrl(),
                    hotel.getCityId()));
        }
        hotelsJson.setLength(hotelsJson.length() - 1);
        hotelsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/hotels")
                        .withQueryStringParameter("cityId", cityId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(hotelsJson.toString()));
    }

    // Register POST /api/v1/hotels
    public void registerAddHotelEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/hotels")
                        .withBody("""
                    {
                        "name": "Hotel 1",
                        "url": "url1",
                        "cityId": "1"
                    }
                    """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                    {
                        "hotelId": "1",
                        "name": "Hotel 1",
                        "url": "url1",
                        "cityId": "1"
                    }
                    """));
    }

    // Register PUT /api/v1/hotels/{hotelId}
    public void registerUpdateHotelEndpoint(String hotelId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/hotels/" + hotelId)
                        .withBody("""
                    {
                        "name": "Hotel 1",
                        "url": "url1",
                        "cityId": "1"
                    }
                    """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                    {
                        "hotelId": "1",
                        "name": "Hotel 1",
                        "url": "url1",
                        "cityId": "1"
                    }
                    """));
    }

    // Register DELETE /api/v1/hotels/{hotelId}
    public void registerDeleteHotelEndpoint(String hotelId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/hotels/" + hotelId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }

    // Register GET /api/v1/hotels/{hotelId} with invalid hotelId
    public void registerGetHotelByIdWithInvalidHotelIdEndpoint(String hotelId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/hotels/" + hotelId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                    {
                        "message": "Hotel not found"
                    }
                    """));
    }

}
