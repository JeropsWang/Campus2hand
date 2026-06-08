package com.productservice.entity.dto.req;

import lombok.Data;

@Data
public class ProductListReq {
    private String category;
    private String title;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}