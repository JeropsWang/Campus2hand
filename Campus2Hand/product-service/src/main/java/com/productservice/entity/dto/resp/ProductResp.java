package com.productservice.entity.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResp {
    private Integer id;
    private String category;
    private String title;
    private String description;
    private BigDecimal price;
    private String quality;
    private String imageUrl;
    private LocalDateTime publishTime;
    private String status;
}