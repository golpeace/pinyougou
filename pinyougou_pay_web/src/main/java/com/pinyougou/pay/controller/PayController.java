package com.pinyougou.pay.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;

import entity.Result;
import util.HttpClient;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private PayService payService;
	
	@RequestMapping("/createNative")
	public Map createNative() {
		try {
			return payService.createNative(SecurityContextHolder.getContext().getAuthentication().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("/queryPayStatus/{out_trade_no}")
	public Result queryPayStatus(@PathVariable("out_trade_no") String out_trade_no) {
		
//		3分钟 = 180s = 60次
		int times = 1;
		while(times<=60) {   //查询60次
			try {
				Thread.sleep(3000);  //睡眠3秒    目的：降低查询频率
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Map resultMap = payService.queryPayStatus(out_trade_no);
			if(resultMap.get("trade_state").equals("SUCCESS")) {
//				修改订单的支付状态
//				payLog
				String transaction_id = (String) resultMap.get("transaction_id");
//				order
				String userId = SecurityContextHolder.getContext().getAuthentication().getName();
				payService.updatePayLogAndOrder(transaction_id,userId);
				
				
				return  new Result(true, "支付成功");
			}
			System.out.println("查询次数:"+times);
			times++;  //没查询一次time加一
		}
		
		return  new Result(false, "支付超时");
		
	}

}
