package com.serjnn.api_gateway.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private int price;

}
