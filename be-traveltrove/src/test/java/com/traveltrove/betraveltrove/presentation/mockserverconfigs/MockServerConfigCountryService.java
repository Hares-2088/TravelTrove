package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.dataaccess.country.Country;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.stereotype.Component;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@Component
public class MockServerConfigCountryService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = startClientAndServer(1081);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/countries/{countryId}
    public void registerGetCountryByIdEndpoint(Country country) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/countries/" + country.getCountryId()))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "countryId": "%s",
                        "name": "%s",
                        "image": "%s"
                    }
                    """, country.getCountryId(), country.getName(), country.getImage())));
    }

    // Register GET /api/v1/countries
    public void registerGetAllCountriesEndpoint(Country... countries) {
        StringBuilder countriesJson = new StringBuilder("[");
        for (Country country : countries) {
            countriesJson.append(String.format("""
                {
                    "countryId": "%s",
                    "name": "%s",
                    "image": "%s"
                },
                """, country.getCountryId(), country.getName(), country.getImage()));
        }
        countriesJson.setLength(countriesJson.length() - 1); // Remove last comma
        countriesJson.append("]");

        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/countries"))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(countriesJson.toString()));
    }

    // Register POST /api/v1/countries
    public void registerAddCountryEndpoint(Country country) {
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/countries")
                        .withBody(String.format("""
                    {
                        "name": "%s",
                        "image": "%s"
                    }
                    """, country.getName(), country.getImage())))
                .respond(HttpResponse.response()
                        .withStatusCode(201)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "countryId": "%s",
                        "name": "%s",
                        "image": "%s"
                    }
                    """, country.getCountryId(), country.getName(), country.getImage())));
    }

    // Register PUT /api/v1/countries/{countryId}
    public void registerUpdateCountryEndpoint(Country country) {
        mockServer.when(HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/countries/" + country.getCountryId())
                        .withBody(String.format("""
                    {
                        "name": "%s",
                        "image": "%s"
                    }
                    """, country.getName(), country.getImage())))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "countryId": "%s",
                        "name": "%s",
                        "image": "%s"
                    }
                    """, country.getCountryId(), country.getName(), country.getImage())));
    }

    // Register DELETE /api/v1/countries/{countryId}
    public void registerDeleteCountryEndpoint(String countryId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/countries/" + countryId))
                .respond(HttpResponse.response()
                        .withStatusCode(204));
    }

    // Register Invalid GET /api/v1/countries/{countryId}
    public void registerGetCountryByInvalidIdEndpoint(String invalidCountryId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/countries/" + invalidCountryId))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "message": "Country id not found: %s"
                    }
                    """, invalidCountryId)));
    }
}
