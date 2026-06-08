package com.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orderservice.entity.dto.req.OrderCreateReq;
import com.orderservice.entity.dto.req.OrderListReq;
import com.orderservice.entity.dto.resp.OrderResp;
import com.orderservice.entity.po.Order;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private static final List<String> VALID_STATUSES = Arrays.asList(
            "PENDING_PAYMENT", "PENDING_DELIVERY", "COMPLETED", "CLOSED"
    );

    private static final Map<String, String> STATUS_DESC_MAP = new HashMap<>();
    private static final Map<String, String> TRADE_TYPE_DESC_MAP = new HashMap<>();

    static {
        STATUS_DESC_MAP.put("PENDING_PAYMENT", "待支付");
        STATUS_DESC_MAP.put("PENDING_DELIVERY", "待交付");
        STATUS_DESC_MAP.put("COMPLETED", "已完成");
        STATUS_DESC_MAP.put("CLOSED", "已关闭");

        TRADE_TYPE_DESC_MAP.put("FACE_TO_FACE", "面交");
        TRADE_TYPE_DESC_MAP.put("SELF_PICKUP", "自提");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResp createOrder(OrderCreateReq req, Integer userId) {
        Order order = new Order();
        BeanUtils.copyProperties(req, order);

        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setQuantity(req.getQuantity());
        order.setTotalAmount(req.getPrice().multiply(new BigDecimal(req.getQuantity())));
        order.setStatus("PENDING_PAYMENT");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        save(order);
        log.info("订单创建成功，订单号: {}, 用户ID: {}", order.getOrderNo(), userId);

        return convertToResp(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResp cancelOrder(Integer orderId, String cancelReason, Integer userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if ("COMPLETED".equals(order.getStatus()) || "CLOSED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        order.setStatus("CLOSED");
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        updateById(order);
        log.info("订单取消成功，订单ID: {}, 原因: {}", orderId, cancelReason);

        return convertToResp(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResp updateOrderStatus(Integer orderId, String status, String tradeLocation) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!VALID_STATUSES.contains(status)) {
            throw new RuntimeException("无效的订单状态");
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        if (StringUtils.hasText(tradeLocation)) {
            order.setTradeLocation(tradeLocation);
        }

        if ("COMPLETED".equals(status)) {
            order.setTradeTime(LocalDateTime.now());
        }

        updateById(order);
        log.info("订单状态更新成功，订单ID: {}, 新状态: {}", orderId, status);

        return convertToResp(order);
    }

    @Override
    public OrderResp getOrderById(Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return null;
        }
        return convertToResp(order);
    }

    @Override
    public IPage<OrderResp> listOrders(OrderListReq req) {
        Page<Order> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (req.getUserId() != null) {
            wrapper.eq(Order::getUserId, req.getUserId());
        }

        if (StringUtils.hasText(req.getStatus())) {
            wrapper.eq(Order::getStatus, req.getStatus());
        }

        if (StringUtils.hasText(req.getKeyword())) {
            wrapper.like(Order::getProductName, req.getKeyword())
                    .or()
                    .like(Order::getOrderNo, req.getKeyword());
        }

        wrapper.orderByDesc(Order::getCreateTime);

        IPage<Order> orderPage = page(page, wrapper);

        IPage<OrderResp> respPage = new Page<>();
        BeanUtils.copyProperties(orderPage, respPage);
        respPage.setRecords(orderPage.getRecords().stream()
                .map(this::convertToResp)
                .toList());

        return respPage;
    }

    @Override
    public IPage<OrderResp> getMyOrders(Integer userId, String status, Integer pageNum, Integer pageSize) {
        OrderListReq req = new OrderListReq();
        req.setUserId(userId);
        req.setStatus(status);
        req.setPageNum(pageNum);
        req.setPageSize(pageSize);
        return listOrders(req);
    }

    /**
     * 转换为响应对象
     */
    private OrderResp convertToResp(Order order) {
        OrderResp resp = new OrderResp();
        BeanUtils.copyProperties(order, resp);
        resp.setStatusDesc(STATUS_DESC_MAP.getOrDefault(order.getStatus(), order.getStatus()));
        resp.setTradeTypeDesc(TRADE_TYPE_DESC_MAP.getOrDefault(order.getTradeType(), order.getTradeType()));
        return resp;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }
}
