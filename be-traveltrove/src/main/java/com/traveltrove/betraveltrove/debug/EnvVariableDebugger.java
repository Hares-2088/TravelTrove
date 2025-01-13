package com.traveltrove.betraveltrove.debug;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("ci")
@Component
public class EnvVariableDebugger {
    private static final Logger logger = LoggerFactory.getLogger(EnvVariableDebugger.class);

    @org.springframework.beans.factory.annotation.Value("${auth0.clientId:NOT_SET}")
    private String clientId;

    @org.springframework.beans.factory.annotation.Value("${auth0.clientSecret:NOT_SET}")
    private String clientSecret;

    @org.springframework.beans.factory.annotation.Value("${spring.profiles.active:NOT_SET}")
    private String activeProfile;

    public EnvVariableDebugger() {
        logger.info("Auth0 Client ID: {}", clientId);
        logger.info("Auth0 Client Secret: {}", clientSecret);
        logger.info("Active Profile: {}", activeProfile);
    }
}
