package com.pinyougou.seckill.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.GoodsIdAndUserId;
import util.IdWorker;

@Component
public class CreateOrder implements Runnable{

	@Autowired
	private RedisTemplate redisTemplate;
	 
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	
	@Override
	public void run() {
		
		GoodsIdAndUserId goodsIdAndUserId = (GoodsIdAndUserId) redisTemplate.boundListOps("saveOrderTask").rightPop();
		Long id = goodsIdAndUserId.getGoodsId();
		String userId = goodsIdAndUserId.getUserId();
		
		TbSeckillGoods seckillGoods =  (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
//		第二步：保存订单
		TbSeckillOrder  seckillOrder = new TbSeckillOrder();
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setId(idWorker.nextId());
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setSeckillId(id);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setStatus("0"); //0代表未支付
		seckillOrder.setUserId(userId);
		seckillOrderMapper.insert(seckillOrder);
		
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
		
//		第三步：减库存 
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
//		第四步：还需要判断库存	 如果减完后库存是0  把此商品从redis中移除并且更新到mysql中
		if(seckillGoods.getStockCount()==0) {
//			如果减完后库存是0
//			把此商品从redis中移除
			 redisTemplate.boundHashOps("seckillGoods").delete(id);
//			并且更新到mysql中
			 seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
		}else {
//			把库存更改后的商品重新在放到redis中
			 redisTemplate.boundHashOps("seckillGoods").put(id, seckillGoods);	
		}
		
//		排队人数消减
		redisTemplate.boundValueOps("seckillGoods_people"+id).increment(-1);//购买此商品的排队人数
		
	}

}
