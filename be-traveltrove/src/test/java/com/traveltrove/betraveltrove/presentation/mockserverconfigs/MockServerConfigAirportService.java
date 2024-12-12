package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.country.Country;
import com.traveltrove.betraveltrove.presentation.airport.AirportRequestModel;
import org.checkerframework.checker.units.qual.C;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;


@Component
public class MockServerConfigAirportService {
    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = startClientAndServer(1084);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }


    public void registerGetAirportByIdEndpoint(Airport airport) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/airports/" + airport.getAirportId()))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                                {
                                    "airportId": "%s",
                                    "name": "Airport %s",
                                    "cityId": "City %s"
                                }
                                """, airport.getAirportId(), airport.getName(), airport.getCityId())));
    }

    public void registerGetAllAirportsEndpoint(Airport... airports) {
        StringBuilder airportsJson = new StringBuilder("[");
        for (Airport airport : airports) {
            airportsJson.append(String.format("""
                    {
                        "airportId": "%s",
                        "name": "Airport %s",
                        "cityId": "City %s"
                    },
                    """, airport.getAirportId(), airport.getName(), airport.getCityId()));
        }
        airportsJson.setLength(airportsJson.length() - 1);
        airportsJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/airports"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(airportsJson.toString()));
    }

    public void registerUpdateAirportEndpoint(Airport airport) {
        mockServer.when(HttpRequest.request()
                .withMethod("PUT")
                .withPath("/api/v1/airports" + airport.getAirportId())
                .withBody(String.format("""
                        {
                            "name": "%s",
                            "cityId": "%s"
                        }                      
                        """, airport.getAirportId(), airport.getName(), airport.getCityId())));
    }

    public void registerDeleteAirportEndpoint(String airportId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/airports" + airportId))
                .respond(HttpResponse.response()
                        .withStatusCode(204));
    }
    public void registerGetAirportByInvalidIdEndpoint(String invalidAirportId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/airports/" + invalidAirportId))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                                {
                                    "message": "Airport id not found: %s"
                                }
                                """, invalidAirportId)));
    }

}
