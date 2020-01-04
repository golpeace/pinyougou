package com.pinyougou.seckill.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;

import entity.Result;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	
	@Reference
	private SeckillService seckillService;
	
	@RequestMapping("/saveOrder/{id}")
	public Result  saveOrder(@PathVariable("id") Long id){
		  try {
			  
		  String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		  if(userId.equals("anonymousUser")) {
			  return new Result(false, "请先登录"); 
		  }
			  
			seckillService.saveOrder(id,userId);
			return new Result(true, "");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "订单保存失败");
		}
	}
	
	
	@RequestMapping("/findAllFromRedis")
	public List<TbSeckillGoods>  findAllFromRedis(){
		return seckillService.findAllFromRedis();
	}
	
	
	@RequestMapping("/findOneFromRedis/{id}")
	public  TbSeckillGoods    findOneFromRedis(@PathVariable("id") Long id){
		return seckillService.findOneFromRedis(id);
	}
	
}
