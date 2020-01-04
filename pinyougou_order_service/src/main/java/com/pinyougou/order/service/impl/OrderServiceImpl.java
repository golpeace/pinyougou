package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.registry.redis.RedisRegistry;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;

import groupEntity.Cart;
import util.IdWorker;
@Service
public class OrderServiceImpl implements OrderService {

	
	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public void add(TbOrder tbOrder) {
		String  cartListStr = (String) redisTemplate.boundValueOps(tbOrder.getUserId()).get();
		List<Cart> cartList = JSON.parseArray(cartListStr, Cart.class);
		String orderListStr="";
		Double totalFee = 0.00;
		for (Cart cart : cartList) {
			TbOrder order = new TbOrder();
//			  `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
			order.setPaymentType(tbOrder.getPaymentType());
//			  `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
			order.setReceiverAreaName(tbOrder.getReceiverAreaName());
//			  `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
			order.setReceiverMobile(tbOrder.getReceiverMobile());
//			  `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
			order.setReceiver(tbOrder.getReceiver());
//			  `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
			order.setSourceType(tbOrder.getSourceType());
			
			
//			`order_id` bigint(20) NOT NULL COMMENT '订单id',
			long orderId = idWorker.nextId();
			
			orderListStr+=orderId+",";
			
			order.setOrderId(orderId);
			
//			`payment` decimal(20,2) DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
			double payment=0.00;
			for(TbOrderItem orderItem: cart.getOrderItemList()) {
				payment+=orderItem.getTotalFee().doubleValue();
				
//				保存订单项
//				  `id` bigint(20) NOT NULL,
//				  `order_id` bigint(20) NOT NULL COMMENT '订单id',
				orderItem.setOrderId(orderId);
				orderItem.setId(idWorker.nextId());
				
				orderItemMapper.insert(orderItem);
				
			}
			totalFee+=payment;
			order.setPayment(new BigDecimal(payment));
//			  `status` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
			order.setStatus("1");
			//		 `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
			order.setCreateTime(new Date());
//			  `update_time` datetime DEFAULT NULL COMMENT '订单更新时间',
			order.setUpdateTime(new Date());
//			  `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
			order.setSellerId(cart.getSellerId());
			
			orderMapper.insert(order);
		}

//		支付日志
		TbPayLog payLog = new TbPayLog();
		payLog.setCreateTime(new Date());
		payLog.setOrderList(orderListStr.substring(0, orderListStr.length()-1));
		payLog.setOutTradeNo(idWorker.nextId()+"");
		payLog.setPayType(tbOrder.getPaymentType());
		payLog.setTotalFee((long) (totalFee*100));
		payLog.setTradeState("0"); // 0 未交易   1 已交易
//		payLog.setTransactionId(transactionId);
		payLog.setUserId(tbOrder.getUserId());
		payLogMapper.insert(payLog);
		
//		支付日志存放到redis中 ，方便在支付时获取
		redisTemplate.boundHashOps("payLog").put(tbOrder.getUserId(), payLog);
		
		
//		最后一步 清空购物车数据
		redisTemplate.delete(tbOrder.getUserId());

		
		
	}

}
