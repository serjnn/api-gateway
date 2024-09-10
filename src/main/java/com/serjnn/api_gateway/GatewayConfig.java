package com.serjnn.api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final LoggingGatewayFilterFactory loggingGatewayFilterFactory;

    public GatewayConfig(LoggingGatewayFilterFactory loggingGatewayFilterFactory) {
        this.loggingGatewayFilterFactory = loggingGatewayFilterFactory;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Маршрут для /order/** с прикрепленным фильтром
                .route("product", r -> r.path("/product/**")
                        .filters(f ->
                                f.rewritePath("/product(?<segment>/?.*)", "$\\{segment}"))

                        .uri("http://localhost:7002"))
                .route("order", r -> r.path("/order/**")
                        .filters(f -> f
                                .rewritePath("/order(?<segment>/?.*)", "${segment}")
                                .filter(loggingGatewayFilterFactory.apply(new LoggingGatewayFilterFactory.Config()))) // Применение фильтра
                        .uri("lb://order"))

                // Маршрут для /bucket/** с прикрепленным фильтром

                .build();
    }
}
