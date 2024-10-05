package com.serjnn.api_gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final  JwtValidationFilterFactory jwtValidationFilterFactory;



    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product", r -> r.path("/product/**")
                        .filters(f ->
                                f.rewritePath("/product(?<segment>/?.*)", "$\\{segment}"))

                        .uri("lb://product"))


                .route("order", r -> r.path("/order/**")
                        .filters(f -> f
                                .rewritePath("/order(?<segment>/?.*)", "${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")

                                .filter(jwtValidationFilterFactory.apply(new JwtValidationFilterFactory.Config())))

                        .uri("lb://order"))


                .route("client", r -> r.path("/client/**")
                        .filters(f -> f.rewritePath("/client(?<segment>/?.*)", "${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE"))
                        .uri("lb://client"))


                .route("bucket", r -> r.path("/bucket/**")
                        .filters(f -> f.rewritePath("/bucket(?<segment>/?.*)", "${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
                                .filter(jwtValidationFilterFactory.apply(new JwtValidationFilterFactory.Config())))


                        .uri("lb://bucket"))

                .route("orchestrator", r -> r.path("/orchestrator/**")
                        .filters(f -> f.rewritePath("/orchestrator(?<segment>/?.*)", "${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
                                .filter(jwtValidationFilterFactory.apply(new JwtValidationFilterFactory.Config())))


                        .uri("lb://orchestrator"))



                .build();
    }
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }


}
