package com.microservices.gateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.addResponseHeader;

/**
 * 🚀 CONFIGURATION DES ROUTES API GATEWAY
 * 
 * Spring Cloud Gateway MVC (non-reactive)
 * Route le trafic vers les microservices
 */
@Configuration
public class GatewayRoutesConfig {

    /**
     * Route vers USER-SERVICE
     */
    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(RequestPredicates.path("/api/users/**"), 
                       HandlerFunctions.http("http://localhost:8081"))
                .before(addRequestHeader("X-Gateway", "api-gateway"))
                .after(addResponseHeader("X-Response-Source", "user-service"))
                .build();
    }

    /**
     * Route vers PRODUCT-SERVICE
     */
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product-service")
                .route(RequestPredicates.path("/api/products/**"), 
                       HandlerFunctions.http("http://localhost:8082"))
                .before(addRequestHeader("X-Gateway", "api-gateway"))
                .after(addResponseHeader("X-Response-Source", "product-service"))
                .build();
    }

    /**
     * Route vers ORDER-SERVICE
     */
    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order-service")
                .route(RequestPredicates.path("/api/orders/**"), 
                       HandlerFunctions.http("http://localhost:8083"))
                .before(addRequestHeader("X-Gateway", "api-gateway"))
                .after(addResponseHeader("X-Response-Source", "order-service"))
                .build();
    }

    /**
     * Route vers GRAPHQL-BFF (endpoint GraphQL)
     */
    @Bean
    public RouterFunction<ServerResponse> graphqlRoute() {
        return GatewayRouterFunctions.route("graphql-bff")
                .route(RequestPredicates.path("/graphql/**"), 
                       HandlerFunctions.http("http://localhost:8084"))
                .before(addRequestHeader("X-Gateway", "api-gateway"))
                .build();
    }

    /**
     * Route vers GRAPHQL-BFF (GraphiQL IDE)
     */
    @Bean
    public RouterFunction<ServerResponse> graphiqlRoute() {
        return GatewayRouterFunctions.route("graphiql")
                .route(RequestPredicates.path("/graphiql/**"), 
                       HandlerFunctions.http("http://localhost:8084"))
                .build();
    }
}
