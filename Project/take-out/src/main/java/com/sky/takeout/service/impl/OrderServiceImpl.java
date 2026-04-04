package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.constant.OrderConstant;
import com.sky.takeout.dto.*;
import com.sky.takeout.entity.OrderDetail;
import com.sky.takeout.entity.Orders;
import com.sky.takeout.entity.ShoppingCart;
import com.sky.takeout.mapper.*;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.service.OrderService;
import com.sky.takeout.utils.UserContext;
import com.sky.takeout.vo.OrderSubmitVO;
import com.sky.takeout.vo.OrderVO;
import com.sky.takeout.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper; //订单主表

    @Autowired
    private OrderDetailMapper orderDetailMapper; //订单明细

    @Autowired
    private ShoppingCartMapper shoppingCartMapper; //购物车

    @Autowired
    private AddressBookMapper addressBookMapper; //地址

    @Autowired
    private UserMapper userMapper;//用户信息

    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    @Transactional(rollbackFor = Exception.class) //抛出Exception就全部回滚
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        //业务校验
        Long currentUserId = UserContext.getCurrentId();

        //1.1校验购物车是否为空
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(currentUserId);
        if (CollectionUtils.isEmpty(shoppingCarts)) {
            throw new RuntimeException("购物车为空，不能下单！");
        }

        //1.2校验地址 todo

        //构建并保存订单主表
        Orders order = new Orders();
        //生成订单号
        //计算订单金额
        String orderNumber = generateOrderNumber();
        order.setNumber(orderNumber);

        //设置订单状态
        order.setStatus(1);

        //设置用户信息
        order.setUserId(currentUserId);
        //todo：查询用户信息
        order.setUserName("测试用户");

        //设置地址信息
        order.setAddressBookId(orderSubmitDTO.getAddressBookId());
        order.setPhone("13800000000");
        order.setAddress("测试地址");
        order.setConsignee("测试收货人");

        //计算购物车总金额
        BigDecimal amount = shoppingCarts.stream()
                .map(cart ->
                        cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setAmount(amount);

        order.setOrderTime(LocalDateTime.now());
        order.setPayMethod(orderSubmitDTO.getPayMethod());
        order.setPayStatus(0);
        order.setRemark(orderSubmitDTO.getRemark());

        orderMapper.insert(order);
        Long orderId = order.getId();

        //保存订单明细数据
        List<OrderDetail> orderDetailList = shoppingCarts.stream()
                .map(cart -> {
                    OrderDetail orderDetail = new OrderDetail();
                    BeanUtils.copyProperties(cart, orderDetail);
                    orderDetail.setId(null);
                    orderDetail.setOrderId(orderId);
                    return orderDetail;
                }).collect(Collectors.toList());

        for (OrderDetail orderDetail : orderDetailList) {
            orderDetailMapper.insert(orderDetail);
        }

        shoppingCartMapper.cleanByUserId(currentUserId);


        // websocket来电提醒
        // 构建推送消息（JSON格式）
        Map<String, Object> wsMessage = new HashMap<>();
        // type=1 表示来单提醒（type=2可以表示催单等其他类型）
        wsMessage.put("type", 1);
        wsMessage.put("orderId", orderId);
        wsMessage.put("content", "您有新订单，订单号：" + orderNumber);

        // 转成JSON字符串推送
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            String wsJson = objectMapper.writeValueAsString(wsMessage);
            webSocketServer.sendToAllClient(wsJson);
            log.info("来单提醒已推送，订单号：{}", orderNumber);
        } catch (Exception e) {
            log.error("来单提醒推送失败：", e);
            // 推送失败不影响下单流程
        }

        return OrderSubmitVO.builder()
                .id(orderId)
                .orderNumber(orderNumber)
                .orderAmount(amount)
                .orderTime(order.getOrderTime())
                .build();
    }

    @Override
    public PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        Page<Orders> page = new Page<>(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(orderPageQueryDTO.getNumber()), Orders::getNumber, orderPageQueryDTO.getNumber());
        queryWrapper.eq(StringUtils.hasText(orderPageQueryDTO.getPhone()), Orders::getPhone, orderPageQueryDTO.getPhone());
        queryWrapper.eq(orderPageQueryDTO.getStatus() != null, Orders::getStatus, orderPageQueryDTO.getStatus());
        queryWrapper.between(orderPageQueryDTO.getBeginTime() != null && orderPageQueryDTO.getEndTime() != null, Orders::getOrderTime, orderPageQueryDTO.getBeginTime(), orderPageQueryDTO.getEndTime());

        queryWrapper.orderByDesc(Orders::getOrderTime);

        Page<Orders> pageResult = orderMapper.selectPage(page, queryWrapper);
        return new PageResult(pageResult.getTotal(), pageResult.getRecords());
    }

    @Override
    @Transactional //保住 原子性 + 隔离性
    public void confirm(OrderConfirmDTO orderConfirmDTO) {
        Long orderId = orderConfirmDTO.getId();
        Orders order = orderMapper.selectById(orderId);

        //校验，当前订单状态必须是2（待接单）
        validateStatusTransition(order, OrderConstant.TO_BE_CONFIRMED, OrderConstant.CONFIRMED);

        //校验通过，更改订单状态
        order.setStatus(OrderConstant.CONFIRMED);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void rejection(OrderCancelDTO orderCancelDTO) {
        Long orderId = orderCancelDTO.getId();
        Orders order = orderMapper.selectById(orderId);

        validateStatusTransition(order, OrderConstant.TO_BE_CONFIRMED, OrderConstant.CANCELLED);

        order.setStatus(OrderConstant.CANCELLED);
        //todo:实际上要记录取消原因
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancel(OrderCancelDTO orderCancelDTO) {
        Long orderId = orderCancelDTO.getId();
        Orders order = orderMapper.selectById(orderId);

        //校验，状态必须是1（待付款） 或 2（待接单）才可取消
        /*初始代码：
        改进处：1.order可能为null，
        Integer currentStatus = order.getStatus();
        if (currentStatus == 1 || currentStatus == 2) {
            order.setStatus(OrderConstant.CANCELLED);
            orderMapper.updateById(order);
        } else
            throw new RuntimeException("当前状态不允许取消");*/

        Integer currentStatus = order.getStatus();
        if (!currentStatus.equals(OrderConstant.PENDING_PAYMENT) && !currentStatus.equals(OrderConstant.TO_BE_CONFIRMED)) {
            throw new RuntimeException("当前状态不允许取消");
        }

        order.setStatus(OrderConstant.CANCELLED);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void delivery(Long id) {
        Orders order = orderMapper.selectById(id);

        validateStatusTransition(order, OrderConstant.CONFIRMED, OrderConstant.IN_DELIVERY_PROGRESS);

        order.setStatus(OrderConstant.IN_DELIVERY_PROGRESS);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void complete(Long id) {
        Orders order = orderMapper.selectById(id);

        validateStatusTransition(order, OrderConstant.IN_DELIVERY_PROGRESS, OrderConstant.COMPLETED);

        order.setStatus(OrderConstant.COMPLETED);
        //设置完成时间
        order.setCheckoutTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        //1.查询的订单存在否
        Orders order = orderMapper.selectById(id);
        if (order == null)
            throw new RuntimeException("订单不存在");

        //2.转换为VO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        //设置状态描述
        orderVO.setStatusDesc(getStatusText(orderVO.getStatus()));

        //设置付款方式描述
        orderVO.setPayMethodDesc(getPayMethodText(orderVO.getPayMethod()));

        //订单明细列表
        orderVO.setOrderDetails(orderDetailMapper.getByOrderId(orderVO.getId()));

        return orderVO;
    }

    @Override
    public PageResult getHistoryOrders(OrderPageQueryDTO dto) {
        Long currentId = UserContext.getCurrentId();

        Page<Orders> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, currentId);
        queryWrapper.like(StringUtils.hasText(dto.getNumber()), Orders::getNumber, dto.getNumber());
        queryWrapper.eq(dto.getStatus() != null, Orders::getStatus, dto.getStatus());
        queryWrapper.between(dto.getBeginTime() != null && dto.getEndTime() != null, Orders::getOrderTime, dto.getBeginTime(), dto.getEndTime());

        queryWrapper.orderByDesc(Orders::getOrderTime);

        Page<Orders> pageResult = orderMapper.selectPage(page, queryWrapper);

        //将Orders转为VO
        List<Orders> list = pageResult.getRecords();
        return new PageResult(pageResult.getTotal(), list.stream()
                .map(o -> {
                    OrderVO vo = new OrderVO();
                    BeanUtils.copyProperties(o, vo);
                    vo.setStatusDesc(getStatusText(vo.getStatus()));
                    vo.setPayMethodDesc(getPayMethodText(vo.getPayMethod()));
                    return vo;
                }).collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payment(OrderPaymentDTO dto) {
        //订单是否存在
        Orders order = orderMapper.getByOrderNumber(dto.getOrderNumber());
        if (order == null) throw new RuntimeException("订单不存在");

        //状态是否为待付款
        Integer status = order.getStatus();
        if (!Objects.equals(OrderConstant.PENDING_PAYMENT, status))
            throw new RuntimeException("订单非待付款状态，状态为 " + getStatusText(status));

        //订单是否已支付
        Integer payStatus = order.getPayStatus();
        if (Objects.equals(OrderConstant.PAID, payStatus))
            throw new RuntimeException("订单已支付");

        //模拟支付成功，更新订单
        Orders updateOrder = Orders.builder()
                .id(order.getId())
                .status(OrderConstant.TO_BE_CONFIRMED)
                .payStatus(OrderConstant.PAID)
                .checkoutTime(LocalDateTime.now())
                .payMethod(dto.getPayment())
                .build();

        //更新订单
        int rows = orderMapper.updateById(updateOrder);
        if (rows != 1)
            throw new RuntimeException("支付失败，订单状态更新异常");

        //记录支付日志（实际保存到支付流水表）
        log.info("订单支付成功, 订单号:{}, 金额{}", order.getNumber(), order.getAmount());

    }

    @Override
    public Map<String, Object> getPaymentStatus(String orderNumber) {
        Orders order = orderMapper.getByOrderNumber(orderNumber);
        if (order == null)
            throw new RuntimeException("订单不存在");
        Map<String, Object> result = new HashMap<>();
        result.put("orderNumber", order.getNumber());
        result.put("payStatus", order.getPayStatus());
        result.put("payStatusDesc", getPayStatusText(order.getPayStatus()));
        result.put("orderStatus", order.getStatus());
        result.put("orderStatusDesc", getStatusText(order.getStatus()));
        result.put("amount", order.getAmount());
        result.put("payMethod", order.getPayMethod());
        return result;
    }

    @Override
    public void reminder(Long id) {
        // 1.订单是否存在
        Orders order = orderMapper.selectById(id);
        if (order == null)
            throw new RuntimeException("订单不存在");

        // 2.推送催单消息
        Map<String, Object> wsMessage = new HashMap<>();
        // type=2 表示催单
        wsMessage.put("type", 2);
        wsMessage.put("orderId", id);
        wsMessage.put("content", "订单号：" + order.getNumber() + " 用户催单啦！");

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            String wsJson = objectMapper.writeValueAsString(wsMessage);
            webSocketServer.sendToAllClient(wsJson);
        } catch (Exception e) {
            log.error("催单推送失败：", e);
        }
    }


    public String generateOrderNumber() {
        //时间戳
        String timePart = String.valueOf(System.currentTimeMillis());
        //随机数
        String randomPart = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));

        return timePart + randomPart;
    }

    //状态流转的通用校验方法
    public void validateStatusTransition(Orders order, Integer expectedStatus, Integer targetStatus) {
        //订单是否存在
        if (order == null)
            throw new RuntimeException("订单不存在");

        //状态是否更改。（符合期望否）
        if (!expectedStatus.equals(order.getStatus()))
            throw new RuntimeException("订单状态已变更，当前状态为 " + order.getStatus());
    }

    //状态码转文本
    public String getStatusText(Integer status) {
        if (status == null)
            return "未知状态";

        switch (status) {
            case OrderConstant.PENDING_PAYMENT: return "待付款";
            case OrderConstant.TO_BE_CONFIRMED: return "待接单";
            case OrderConstant.CONFIRMED: return "已接单";
            case OrderConstant.IN_DELIVERY_PROGRESS: return "配送中";
            case OrderConstant.COMPLETED: return "已完成";
            case OrderConstant.CANCELLED: return "已取消";
            case OrderConstant.REFUNDED: return "已退款";
            default: return "未知(" + status + ")";
        }
    }

    //付款方式码转文本
    public String getPayMethodText(Integer payMethod) {
        if (payMethod == null)
            throw new RuntimeException("未知");

        switch (payMethod) {
            case OrderConstant.UN_PAID: return "未付款";
            case OrderConstant.WECHAT_PAY: return "微信支付";
            case OrderConstant.ALIPAY: return "支付宝支付";
            default: return "其它(" + payMethod + ")";
        }
    }

    //支付状态转文本
    public String getPayStatusText(Integer payStatus) {
        if (payStatus == null)
            throw new RuntimeException("未知");

        switch (payStatus) {
            case OrderConstant.UN_PAID: return "未支付";
            case OrderConstant.PAID: return "已支付";
            case OrderConstant.REFUND: return "已退款";
            default: return "其他（" + payStatus + ")";
        }
    }
}
