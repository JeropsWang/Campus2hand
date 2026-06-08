package com.orderservice.entity.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应
 */
@Data
public class OrderResp {

    private Integer id;

    private String orderNo;

    private Integer userId;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;

    private String status;

    private String statusDesc;

    private String tradeType;

    private String tradeTypeDesc;

    private String tradeLocation;

    private LocalDateTime tradeTime;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String cancelReason;

    private LocalDateTime cancelTime;
}
