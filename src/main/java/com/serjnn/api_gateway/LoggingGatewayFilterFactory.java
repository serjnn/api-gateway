package com.serjnn.api_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGatewayFilterFactory extends
        AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

        @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            log.info("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", exchange.getRequest().getURI());

            // Устанавливаем статус запрета доступа
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

            // Завершаем обработку запроса, не пропуская его дальше
            return exchange.getResponse().setComplete();
        };
    }
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            // Логируем начало фильтра
//
//
//            // Пропускаем запрос дальше
//            return chain.filter(exchange)
//                    .then(Mono.fromRunnable(() -> {
//                        // Логируем завершение обработки запроса
//
//                    }));
//        };
//    }


    public static class Config {
        // Место для конфигурации, если нужно
    }
}
