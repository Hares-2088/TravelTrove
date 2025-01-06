package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

@Component
public class MockServerConfigPackageService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1082); // Unique port for Package mock server
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // Register GET /api/v1/packages
    public void registerGetAllPackagesEndpoint() {
        String packagesJson = """
        [
            {
                "packageId": "1",
                "tourId": "1",
                "airportId": "1",
                "name": "Package 1",
                "description": "Package 1 Description",
                "startDate": "2021-01-01",
                "endDate": "2021-01-10",
                "priceSingle": 100.0,
                "priceDouble": 200.0,
                "priceTriple": 300.0,
                "availableSeats": 90,
                "totalSeats": 100,
                "packageStatus": "EXPIRED"
            },
            {
                "packageId": "2",
                "tourId": "1",
                "airportId": "1",
                "name": "Package 2",
                "description": "Package 2 Description",
                "startDate": "2021-02-01",
                "endDate": "2021-02-10",
                "priceSingle": 100.0,
                "priceDouble": 200.0,
                "priceTriple": 300.0,
                "availableSeats": 90,
                "totalSeats": 100,
                "packageStatus": "EXPIRED"
            }
        ]
        """;

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/packages"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(packagesJson));
    }

    // Register GET /api/v1/packages with tourId query parameter
    public void registerGetAllPackagesByTourIdEndpoint(String tourId) {
        String packagesJson = """
        [
            {
                "packageId": "1",
                "tourId": "1",
                "airportId": "1",
                "name": "Package 1",
                "description": "Package 1 Description",
                "startDate": "2021-01-01",
                "endDate": "2021-01-10",
                "priceSingle": 100.0,
                "priceDouble": 200.0,
                "priceTriple": 300.0,
                "availableSeats": 90,
                "totalSeats": 100,
                "packageStatus": "EXPIRED"
            },
            {
                "packageId": "2",
                "tourId": "1",
                "airportId": "1",
                "name": "Package 2",
                "description": "Package 2 Description",
                "startDate": "2021-02-01",
                "endDate": "2021-02-10",
                "priceSingle": 100.0,
                "priceDouble": 200.0,
                "priceTriple": 300.0,
                "availableSeats": 90,
                "totalSeats": 100,
                "packageStatus": "EXPIRED"
            }
        ]
        """;

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/packages")
                        .withQueryStringParameter("tourId", tourId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(packagesJson));
    }

    // Register GET /api/v1/packages/{packageId}
    public void registerGetPackageByPackageIdEndpoint(String packageId) {
        String packageJson = """
        {
            "packageId": "1",
            "tourId": "1",
            "airportId": "1",
            "name": "Package 1",
            "description": "Package 1 Description",
            "startDate": "2021-01-01",
            "endDate": "2021-01-10",
            "priceSingle": 100.0,
            "priceDouble": 200.0,
            "priceTriple": 300.0,
            "availableSeats": 90,
            "totalSeats": 100,
            "packageStatus": "EXPIRED"
        }
        """;

        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/packages/" + packageId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(packageJson));
    }

    // Register POST /api/v1/packages
    public void registerCreatePackageEndpoint() {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/packages")
                .withBody("""
                        {
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "totalSeats": 100
                        }
                        """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(201)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 90,
                            "totalSeats": 100,
                            "packageStatus": "EXPIRED"
                        }
                        """));
    }

    // Register PUT /api/v1/packages/{packageId}
    public void registerUpdatePackageEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/packages/" + packageId)
                        .withBody("""
                        {
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "totalSeats": 100
                        }
                        """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 90,
                            "totalSeats": 100,
                            "packageStatus": "EXPIRED"
                        }
                        """));
    }

    // Register DELETE /api/v1/packages/{packageId}
    public void registerDeletePackageEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/api/v1/packages/" + packageId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(204));
    }

    // Register GET /api/v1/packages/{invalidId}
    public void registerGetPackageByInvalidIdEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/packages/" + packageId))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                        {
                            "message": "Package not found"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/decreaseAvailableSeats
    public void registerDecreaseAvailableSeatsEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/decreaseAvailableSeats")
                        .withQueryStringParameter("quantity", "1"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 89,
                            "totalSeats": 100,
                            "packageStatus": "EXPIRED"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{invalidId}/decreaseAvailableSeats
    public void registerDecreaseAvailableSeatsWithInvalidIdEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/decreaseAvailableSeats")
                        .withQueryStringParameter("quantity", "1"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                        {
                            "message": "Package not found"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/decreaseAvailableSeats with invalid quantity
    public void registerDecreaseAvailableSeatsWithInvalidQuantityEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/decreaseAvailableSeats")
                        .withQueryStringParameter("quantity", "101"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(400)
                        .withBody("""
                        {
                            "message": "Invalid quantity"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/increaseAvailableSeats
    public void registerIncreaseAvailableSeatsEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/increaseAvailableSeats")
                        .withQueryStringParameter("quantity", "1"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 91,
                            "totalSeats": 100,
                            "packageStatus": "EXPIRED"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{invalidId}/increaseAvailableSeats
    public void registerIncreaseAvailableSeatsWithInvalidIdEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/increaseAvailableSeats")
                        .withQueryStringParameter("quantity", "1"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                        {
                            "message": "Package not found"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/increaseAvailableSeats with invalid quantity
    public void registerIncreaseAvailableSeatsWithInvalidQuantityEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/increaseAvailableSeats")
                        .withQueryStringParameter("quantity", "101"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(400)
                        .withBody("""
                        {
                            "message": "Invalid quantity"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/refreshAvailableSeats
    public void registerRefreshAvailableSeatsEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/refreshAvailableSeats"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 90,
                            "totalSeats": 100,
                            "packageStatus": "EXPIRED"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{invalidId}/refreshAvailableSeats
    public void registerRefreshAvailableSeatsWithInvalidIdEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/refreshAvailableSeats"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                        {
                            "message": "Package not found"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{packageId}/updatePackageStatus
    public void registerUpdatePackageStatusEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/updatePackageStatus")
                        .withQueryStringParameter("packageStatus", "CLOSED"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "tourId": "1",
                            "airportId": "1",
                            "name": "Package 1",
                            "description": "Package 1 Description",
                            "startDate": "2021-01-01",
                            "endDate": "2021-01-10",
                            "priceSingle": 100.0,
                            "priceDouble": 200.0,
                            "priceTriple": 300.0,
                            "availableSeats": 90,
                            "totalSeats": 100,
                            "packageStatus": "CLOSED"
                        }
                        """));
    }

    // Register PATCH /api/v1/packages/{invalidId}/updatePackageStatus
    public void registerUpdatePackageStatusWithInvalidIdEndpoint(String packageId) {
        mockServer.when(org.mockserver.model.HttpRequest.request()
                        .withMethod("PATCH")
                        .withPath("/api/v1/packages/" + packageId + "/updatePackageStatus")
                        .withQueryStringParameter("packageStatus", "CLOSED"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withBody("""
                        {
                            "message": "Package not found"
                        }
                        """));
    }


}
