package com.productservice.entity.dto.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductListReq {
    private String category;
    private String title;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}