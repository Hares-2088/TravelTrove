package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.city.City;
import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

@Component
public class MockServerConfigCityService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1087);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/cities/{cityId}
    public void registerGetCityByIdEndpoint(City city) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/cities/" + city.getCityId()))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "cityId": "%s",
                        "name": "%s",
                        "countryId": "%s"
                    }
                    """, city.getCityId(), city.getName(), city.getCountryId())));
    }

    // Register GET /api/v1/cities
    public void registerGetAllCitiesEndpoint(City... cities) {
        StringBuilder citiesJson = new StringBuilder("[");
        for (City city : cities) {
            citiesJson.append(String.format("""
                {
                    "cityId": "%s",
                    "name": "%s",
                    "countryId": "%s"
                },
                """, city.getCityId(), city.getName(), city.getCountryId()));
        }
        citiesJson.setLength(citiesJson.length() - 1); // Remove last comma
        citiesJson.append("]");

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/cities"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(citiesJson.toString()));
    }

    // Register Invalid GET /api/v1/cities/{cityId}
    public void registerGetCityByInvalidIdEndpoint(String cityId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/cities/" + cityId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404));
    }

}
