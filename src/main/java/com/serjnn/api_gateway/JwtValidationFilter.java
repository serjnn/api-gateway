package com.serjnn.api_gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {


    public static class Config {
        // Добавьте параметры конфигурации при необходимости
    }

    public JwtValidationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Логируем начало обработки фильтра
            log.info("JwtValidationFilter: запрос на маршрут {}", exchange.getRequest().getURI());
            System.out.println("ahahahahahahaahahaa");
            // Извлекаем JWT-токен из заголовков
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("Токен отсутствует или не начинается с 'Bearer '");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String jwtToken = authHeader.substring(7); // Убираем "Bearer "
            log.info("Извлечен JWT токен: {}", jwtToken);

            // Подготовка запроса на валидацию
            RestTemplate restTemplate = new RestTemplate();
            String validationUrl = "http://localhost:7004/api/v1/validate";
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

            try {
                // Выполняем POST-запрос на сервер валидации
                ResponseEntity<String> response = restTemplate.exchange(validationUrl, HttpMethod.POST, requestEntity, String.class);

                // Если ответ успешный (статус 200), пропускаем запрос дальше
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("JWT токен успешно валидирован");
                    return chain.filter(exchange);
                } else {
                    log.info("Валидация JWT не прошла, статус: {}", response.getStatusCode());
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            } catch (Exception e) {
                log.info("Ошибка при валидации токена: {}", e.getMessage(), e);
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return exchange.getResponse().setComplete();
            }
        };
    }

}

