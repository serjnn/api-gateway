//package com.serjnn.api_gateway;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//@Slf4j
//public class CustomFilter implements GlobalFilter {
//
//
//
//    @Override
//    public Mono<Void> filter(
//            ServerWebExchange exchange,
//            GatewayFilterChain chain) {
//        log.info("аааааааааааааааааааааааааааааааааааааааааааааа");
//        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//        return chain.filter(exchange);
//    }
//}