package com.pinyougou.seckill.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;

@Component
public class SeckillGoodsTask{
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Scheduled(cron="0 1 18 16 8 ?")
	public void addSeckillGoodsToRedis() {
//		把参与秒杀的商品从mysql中读取 放到redis中
//		1、审核通过
//		2、时间范围之内
//		3、有存库的
		
		
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		example.createCriteria().andStatusEqualTo("1")  //审核通过
								.andStockCountGreaterThan(0)
//								endTime大于等于当前时间
//								startTime小于等于当前时间
								.andEndTimeGreaterThanOrEqualTo(new Date())
								.andStartTimeLessThanOrEqualTo(new Date());
		List<TbSeckillGoods> seckillGoodList = seckillGoodsMapper.selectByExample(example );
		
		for (TbSeckillGoods tbSeckillGoods : seckillGoodList) {
			redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
//			每个商品需要压栈  每个商品有多少库存 亚多少次
			for (int i = 0; i < tbSeckillGoods.getStockCount(); i++) {
				redisTemplate.boundListOps("seckillGoods_count"+tbSeckillGoods.getId()).leftPush(tbSeckillGoods.getId());
			 System.out.println(tbSeckillGoods.getTitle()+"--压栈成功");
			}
		}
		System.out.println("秒杀商品已入库");
	}

}
