package com.orderservice.entity.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 取消订单请求
 */
@Data
public class OrderCancelReq {

    @NotNull(message = "订单ID不能为空")
    private Integer orderId;

    private String cancelReason;
}
