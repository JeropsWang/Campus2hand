package com.orderservice.entity.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateReq {

    @NotNull(message = "商品ID不能为空")
    private Integer productId;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    private String productImage;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @NotBlank(message = "交易方式不能为空")
    private String tradeType;

    private String tradeLocation;

    private String remark;
}
