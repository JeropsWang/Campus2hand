package com.productservice.entity.dto;


import lombok.Data;

@Data
public class ProductListDTO {
    private String category;
    private String title;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}