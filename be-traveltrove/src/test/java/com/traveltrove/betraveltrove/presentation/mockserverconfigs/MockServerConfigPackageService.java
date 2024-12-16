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
                "seq": 1,
                "tourId": "1",
                "packageName": "Package 1",
                "packageDescription": "Package 1 Description",
                "packagePrice": 100.0,
                "packageStartDate": "2021-01-01",
                "packageEndDate": "2021-01-10",
                "packageDuration": 10,
                "packageLocation": "Location 1",
                "packageImage": "Image 1"
            },
            {
                "packageId": "2",
                "seq": 2,
                "tourId": "1",
                "packageName": "Package 2",
                "packageDescription": "Package 2 Description",
                "packagePrice": 200.0,
                "packageStartDate": "2021-02-01",
                "packageEndDate": "2021-02-10",
                "packageDuration": 10,
                "packageLocation": "Location 2",
                "packageImage": "Image 2"
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
                "seq": 1,
                "tourId": "1",
                "packageName": "Package 1",
                "packageDescription": "Package 1 Description",
                "packagePrice": 100.0,
                "packageStartDate": "2021-01-01",
                "packageEndDate": "2021-01-10",
                "packageDuration": 10,
                "packageLocation": "Location 1",
                "packageImage": "Image 1"
            },
            {
                "packageId": "2",
                "seq": 2,
                "tourId": "1",
                "packageName": "Package 2",
                "packageDescription": "Package 2 Description",
                "packagePrice": 200.0,
                "packageStartDate": "2021-02-01",
                "packageEndDate": "2021-02-10",
                "packageDuration": 10,
                "packageLocation": "Location 2",
                "packageImage": "Image 2"
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
            "seq": 1,
            "tourId": "1",
            "packageName": "Package 1",
            "packageDescription": "Package 1 Description",
            "packagePrice": 100.0,
            "packageStartDate": "2021-01-01",
            "packageEndDate": "2021-01-10",
            "packageDuration": 10,
            "packageLocation": "Location 1",
            "packageImage": "Image 1"
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
                            "packageName": "Package 1",
                            "packageDescription": "Package 1 Description",
                            "packagePrice": 100.0,
                            "packageStartDate": "2021-01-01",
                            "packageEndDate": "2021-01-10",
                            "packageDuration": 10,
                            "packageLocation": "Location 1",
                            "packageImage": "Image 1"
                        }
                        """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(201)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "seq": 1,
                            "tourId": "1",
                            "packageName": "Package 1",
                            "packageDescription": "Package 1 Description",
                            "packagePrice": 100.0,
                            "packageStartDate": "2021-01-01",
                            "packageEndDate": "2021-01-10",
                            "packageDuration": 10,
                            "packageLocation": "Location 1",
                            "packageImage": "Image 1"
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
                            "seq": 2,
                            "tourId": "1",
                            "packageName": "Updated Package 1",
                            "packageDescription": "Updated Package 1 Description",
                            "packagePrice": 200.0,
                            "packageStartDate": "2021-02-01",
                            "packageEndDate": "2021-02-10",
                            "packageDuration": 10,
                            "packageLocation": "Updated Location 1",
                            "packageImage": "Updated Image 1"
                        }
                        """))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody("""
                        {
                            "packageId": "1",
                            "seq": 2,
                            "tourId": "1",
                            "packageName": "Updated Package 1",
                            "packageDescription": "Updated Package 1 Description",
                            "packagePrice": 200.0,
                            "packageStartDate": "2021-02-01",
                            "packageEndDate": "2021-02-10",
                            "packageDuration": 10,
                            "packageLocation": "Updated Location 1",
                            "packageImage": "Updated Image 1"
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
}
