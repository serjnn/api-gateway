package com.serjnn.api_gateway;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class JwtValidationFilterFactory extends AbstractGatewayFilterFactory<JwtValidationFilterFactory.Config> {

    private final WebClient webClient;

    public static class Config {
    }

    public JwtValidationFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {


            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String jwtToken = authHeader.substring(7);

            String validationUrl = "lb://client/api/v1/secured";
            return webClient.get()
                    .uri(validationUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> {
                        if (response.getStatusCode().is2xxSuccessful()) {

                            return chain.filter(exchange);
                        } else {

                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    })
                    .onErrorResume(e -> {

                        exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                        return exchange.getResponse().setComplete();
                    });
        };
    }
}

