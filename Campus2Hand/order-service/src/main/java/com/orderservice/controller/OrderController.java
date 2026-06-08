package com.orderservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.orderservice.entity.dto.req.OrderCancelReq;
import com.orderservice.entity.dto.req.OrderCreateReq;
import com.orderservice.entity.dto.req.OrderListReq;
import com.orderservice.entity.dto.resp.OrderResp;
import com.orderservice.service.IOrderService;
import com.orderservice.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单")
    public ResponseEntity<Result<OrderResp>> createOrder(
            @Valid @RequestBody OrderCreateReq req,
            @Parameter(description = "用户ID") @RequestParam Integer userId) {
        try {
            OrderResp order = orderService.createOrder(req, userId);
            return ResponseEntity.ok(Result.success(order));
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消订单")
    public ResponseEntity<Result<OrderResp>> cancelOrder(
            @Valid @RequestBody OrderCancelReq req,
            @Parameter(description = "用户ID") @RequestParam Integer userId) {
        try {
            OrderResp order = orderService.cancelOrder(req.getOrderId(), req.getCancelReason(), userId);
            return ResponseEntity.ok(Result.success(order));
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    @PutMapping("/status")
    @Operation(summary = "更新订单状态")
    public ResponseEntity<Result<OrderResp>> updateOrderStatus(
            @Parameter(description = "订单ID") @RequestParam Integer orderId,
            @Parameter(description = "订单状态") @RequestParam String status,
            @Parameter(description = "交易地点") @RequestParam(required = false) String tradeLocation) {
        try {
            OrderResp order = orderService.updateOrderStatus(orderId, status, tradeLocation);
            return ResponseEntity.ok(Result.success(order));
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单详情")
    public ResponseEntity<Result<OrderResp>> getOrderById(@PathVariable Integer orderId) {
        try {
            OrderResp order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.ok(Result.error(404, "订单不存在"));
            }
            return ResponseEntity.ok(Result.success(order));
        } catch (Exception e) {
            log.error("查询订单详情失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    @GetMapping("/list")
    @Operation(summary = "订单列表查询")
    public ResponseEntity<Result<IPage<OrderResp>>> listOrders(OrderListReq req) {
        try {
            IPage<OrderResp> orders = orderService.listOrders(req);
            return ResponseEntity.ok(Result.success(orders));
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    @Operation(summary = "我的订单")
    public ResponseEntity<Result<IPage<OrderResp>>> getMyOrders(
            @Parameter(description = "用户ID") @RequestParam Integer userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<OrderResp> orders = orderService.getMyOrders(userId, status, pageNum, pageSize);
            return ResponseEntity.ok(Result.success(orders));
        } catch (Exception e) {
            log.error("查询我的订单失败", e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }
}
