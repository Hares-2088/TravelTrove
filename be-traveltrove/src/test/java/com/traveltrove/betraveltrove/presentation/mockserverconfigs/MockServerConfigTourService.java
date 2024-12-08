//package com.traveltrove.betraveltrove.presentation.mockserverconfigs;
//
//import com.traveltrove.betraveltrove.dataaccess.tour.Tour;
//import org.mockserver.integration.ClientAndServer;
//import org.mockserver.model.HttpRequest;
//import org.mockserver.model.HttpResponse;
//import org.springframework.stereotype.Component;
//
//import static org.mockserver.integration.ClientAndServer.startClientAndServer;
//
//@Component
//public class MockServerConfigTourService {
//
//    private ClientAndServer mockServer;
//
//    public void startMockServer() {
//        mockServer = startClientAndServer(1080);
//    }
//
//    public void stopMockServer() {
//        if (mockServer != null) {
//            mockServer.stop();
//        }
//    }
//
//    public void registerGetTourByIdEndpoint(Tour tour) {
//        mockServer
//                .when(HttpRequest.request()
//                        .withMethod("GET")
//                        .withPath("/api/v1/tours/" + tour.getTourId()))
//                .respond(HttpResponse.response()
//                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
//                        .withBody(String.format("""
//                                {
//                                    "tourId": "%s",
//                                    "name": "%s",
//                                    "startDate": "%s",
//                                    "endDate": "%s",
//                                    "overallDescription": "%s",
//                                    "available": %s,
//                                    "price": %s,
//                                    "spotsAvailable": %s
//                                }
//                                """,
//                                tour.getTourId(),
//                                tour.getName(),
//                                tour.getStartDate(),
//                                tour.getEndDate(),
//                                tour.getOverallDescription(),
//                                tour.isAvailable(),
//                                tour.getPrice(),
//                                tour.getSpotsAvailable())));
//    }
//
//    public void registerGetTourByInvalidIdEndpoint(String invalidTourId) {
//        mockServer
//                .when(HttpRequest.request()
//                        .withMethod("GET")
//                        .withPath("/api/v1/tours/" + invalidTourId))
//                .respond(HttpResponse.response()
//                        .withStatusCode(404)
//                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
//                        .withBody(String.format("""
//                                {
//                                    "message": "Tour id not found: %s"
//                                }
//                                """, invalidTourId)));
//    }
//}
