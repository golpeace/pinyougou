package com.pinyougou.seckill.service;

import java.util.List;

import com.pinyougou.pojo.TbSeckillGoods;

public interface SeckillService {

	List<TbSeckillGoods> findAllFromRedis();

	TbSeckillGoods findOneFromRedis(Long id);

	void saveOrder(Long id,String userId);

}
