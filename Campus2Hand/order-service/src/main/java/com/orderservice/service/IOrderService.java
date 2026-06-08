package com.orderservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.orderservice.entity.dto.req.OrderCreateReq;
import com.orderservice.entity.dto.req.OrderListReq;
import com.orderservice.entity.dto.resp.OrderResp;
import com.orderservice.entity.po.Order;

/**
 * 订单服务接口
 */
public interface IOrderService extends IService<Order> {

    /**
     * 创建订单
     */
    OrderResp createOrder(OrderCreateReq req, Integer userId);

    /**
     * 取消订单
     */
    OrderResp cancelOrder(Integer orderId, String cancelReason, Integer userId);

    /**
     * 更新订单状态
     */
    OrderResp updateOrderStatus(Integer orderId, String status, String tradeLocation);

    /**
     * 查询订单详情
     */
    OrderResp getOrderById(Integer orderId);

    /**
     * 订单列表查询
     */
    IPage<OrderResp> listOrders(OrderListReq req);

    /**
     * 我的订单
     */
    IPage<OrderResp> getMyOrders(Integer userId, String status, Integer pageNum, Integer pageSize);
}
