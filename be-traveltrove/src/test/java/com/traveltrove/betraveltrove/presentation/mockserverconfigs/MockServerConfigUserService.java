package com.traveltrove.betraveltrove.presentation.mockserverconfigs;

import com.traveltrove.betraveltrove.presentation.user.UserResponseModel;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.stereotype.Component;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@Component
public class MockServerConfigUserService {

    private ClientAndServer mockServer;

    public void startMockServer() {
        mockServer = startClientAndServer(1086);
    }

    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    public void registerGetUserEndpoint(UserResponseModel user) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/users/" + user.getUserId()))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "userId": "%s",
                        "email": "%s",
                        "firstName": "%s",
                        "lastName": "%s",
                        "roles": %s,
                        "permissions": %s
                    }
                    """,
                                user.getUserId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getRoles(),
                                user.getPermissions())));
    }

    // Register Invalid GET /api/v1/users/{userId}
    public void registerInvalidGetUserEndpoint(String invalidUserId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v1/users/" + invalidUserId))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "message": "User ID not found: %s"
                    }
                    """, invalidUserId)));
    }

    // Register POST /api/v1/users/{userId}/login
    public void registerHandleUserLoginEndpoint(UserResponseModel user) {
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/users/" + user.getUserId() + "/login"))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "userId": "%s",
                        "email": "%s",
                        "firstName": "%s",
                        "lastName": "%s",
                        "roles": %s,
                        "permissions": %s
                    }
                    """,
                                user.getUserId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getRoles(),
                                user.getPermissions())));
    }

    // Register PUT /api/v1/users/{userId}/sync
    public void registerSyncUserEndpoint(UserResponseModel updatedUser) {
        mockServer.when(HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/users/" + updatedUser.getUserId() + "/sync"))
                .respond(HttpResponse.response()
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "userId": "%s",
                        "email": "%s",
                        "firstName": "%s",
                        "lastName": "%s",
                        "roles": %s,
                        "permissions": %s
                    }
                    """,
                                updatedUser.getUserId(),
                                updatedUser.getEmail(),
                                updatedUser.getFirstName(),
                                updatedUser.getLastName(),
                                updatedUser.getRoles(),
                                updatedUser.getPermissions())));
    }

    // Register Invalid POST /api/v1/users/{userId}/login
    public void registerInvalidUserLoginEndpoint(String invalidUserId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/v1/users/" + invalidUserId + "/login"))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "message": "User ID not found: %s"
                    }
                    """, invalidUserId)));
    }

    // Register Invalid PUT /api/v1/users/{userId}/sync
    public void registerInvalidUserSyncEndpoint(String invalidUserId) {
        mockServer.when(HttpRequest.request()
                        .withMethod("PUT")
                        .withPath("/api/v1/users/" + invalidUserId + "/sync"))
                .respond(HttpResponse.response()
                        .withStatusCode(404)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(String.format("""
                    {
                        "message": "User ID not found: %s"
                    }
                    """, invalidUserId)));
    }
}

