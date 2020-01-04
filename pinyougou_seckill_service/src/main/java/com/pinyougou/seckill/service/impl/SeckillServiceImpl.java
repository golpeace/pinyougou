package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillService;

import entity.GoodsIdAndUserId;
import util.IdWorker;

@Service
public class SeckillServiceImpl implements SeckillService {

	@Autowired
	private RedisTemplate redisTemplate;
	 
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private CreateOrder createOrder;
	
	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	
	@Autowired
	private ThreadPoolTaskExecutor executor;
	
	
	@Override
	public List<TbSeckillGoods> findAllFromRedis() {
		return redisTemplate.boundHashOps("seckillGoods").values();
	}

	@Override
	public TbSeckillGoods findOneFromRedis(Long id) {
		return  (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
	}

	@Override
	public void saveOrder(Long id,String userId) {
		
		TbSeckillGoods seckillGoods =  (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
//		redisTemplate.boundValueOps("seckillGoods_people"+id).increment(1);//数量加
//		购买之前判断有多少人排队
		Long increment = redisTemplate.boundValueOps("seckillGoods_people"+id).increment(0);//获取"seckillGoods_people"+id对应的值
		
//		假如商品存库是100  如果有超过300人买此商品 应该给提示：排队人数过多
		if(increment>seckillGoods.getStockCount()+200) {
			throw new RuntimeException("排队人数过多");
		}
		
//		商品只有两件  保证每个人能精准地判断出库存
		Long seckillGoodsId = (Long) redisTemplate.boundListOps("seckillGoods_count"+id).rightPop();  //商品id出栈
		if(seckillGoodsId==null) {
			throw new RuntimeException("商品已售罄");
		}
		
//		第一步：从redis中获取商品信息
//		TbSeckillGoods seckillGoods =  (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
//		if(seckillGoods==null) {
//			throw new RuntimeException("商品已售罄");
//		}
		
//		判断用户是否重复购买 未付款时
		Object object = redisTemplate.boundHashOps("seckillOrder").get(userId);
		if(object!=null) {
			throw new RuntimeException("您有未付款的商品，请完成支付再购买");
		}
		
//		订单保存到mysql
		
//		把id和userId放到redis中 压栈
		redisTemplate.boundListOps("saveOrderTask").leftPush(new GoodsIdAndUserId(id,userId));

		
//		记录购买此商品的人数
		redisTemplate.boundValueOps("seckillGoods_people"+id).increment(1);//数量加
		
//		调用多线程  实现的是订单保存
		executor.execute(createOrder);
		
	}

}
