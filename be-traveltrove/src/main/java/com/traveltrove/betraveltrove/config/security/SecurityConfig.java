package com.traveltrove.betraveltrove.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final GlobalErrorHandler errorHandler;

    @Value("${frontend.domain}")
    private String frontendDomain;

    @Value("${okta.oauth2.issuer}")
    private String auth0Domain;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        log.info("Initializing Security Web Filter Chain...");

        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeExchange(authz -> {
                    log.info("Configuring endpoint permissions...");
                    authz
                            .pathMatchers("/api/v1/tours", "/api/v1/tours/**", "/api/v1/packages/**").permitAll() // Allow unauthenticated access
                            .pathMatchers("/api/v1/payments/webhook").permitAll()
                            .anyExchange().authenticated();
                    log.info("Finished configuring endpoint permissions.");
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .oauth2ResourceServer(oauth2ResourceServer -> {
                    log.info("Setting up OAuth2 Resource Server...");
                    oauth2ResourceServer
                            .accessDeniedHandler((exchange, denied) -> {
                                log.error("Access denied: {}", denied.getMessage());
                                return errorHandler.handleAccessDenied(exchange, denied);
                            })
                            .authenticationEntryPoint((exchange, authError) -> {
                                log.error("Authentication error: {}", authError.getMessage());
                                return errorHandler.handleAuthenticationError(exchange, authError);
                            })
                            .jwt(jwt -> {
                                log.info("Configuring JWT authentication converter...");
                                jwt.jwtAuthenticationConverter(this.makePermissionsConverter());
                            });
                    log.info("OAuth2 Resource Server setup completed.");
                })
                .build();
    }

    private ReactiveJwtAuthenticationConverter makePermissionsConverter() {
        log.info("Creating JWT Authentication Converter...");

        final var jwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtAuthoritiesConverter.setAuthoritiesClaimName("permissions");
        jwtAuthoritiesConverter.setAuthorityPrefix("");

        log.info("JWT Authorities Converter configured with claim 'permissions' and no prefix.");

        final var reactiveJwtConverter = new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtAuthoritiesConverter);
        final var jwtAuthConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(reactiveJwtConverter);

        log.info("JWT Authentication Converter fully initialized.");
        return jwtAuthConverter;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin(frontendDomain);
        corsConfig.addAllowedOrigin(auth0Domain);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }

}
