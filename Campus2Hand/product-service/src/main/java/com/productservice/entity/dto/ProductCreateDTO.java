package com.productservice.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    private String category;
    private String title;
    private String description;
    private BigDecimal price;
    private String quality;
    private String imageUrl;
}