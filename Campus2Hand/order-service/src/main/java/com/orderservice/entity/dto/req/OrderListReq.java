package com.orderservice.entity.dto.req;

import lombok.Data;

/**
 * 订单列表查询请求
 */
@Data
public class OrderListReq {

    private Integer userId;

    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
