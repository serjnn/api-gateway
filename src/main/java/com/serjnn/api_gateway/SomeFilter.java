package com.serjnn.api_gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class SomeFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("djasdifjidsfj");
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Если токена нет, возвращаем 401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String jwtToken = authHeader.substring(7); // Убираем "Bearer "

        // Подготовка запроса на валидацию
        RestTemplate restTemplate = new RestTemplate();
        String validationUrl = "http://localhost:7004/api/v1/validate";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken); // Добавляем JWT в заголовок
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers); // Передаем заголовок с токеном

        // Выполняем POST-запрос на сервер валидации
        ResponseEntity<String> response = restTemplate.exchange(validationUrl, HttpMethod.POST, requestEntity, String.class);

        // Если ответ успешный (статус 200), пропускаем запрос дальше
        if (response.getStatusCode().is2xxSuccessful()) {
            return chain.filter(exchange);
        } else {
            // Если ответ не 200, блокируем запрос с кодом 403 Forbidden
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }
}