package com.pinyougou.itempage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.itempage.service.ItemPageService;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import groupEntity.Goods;

@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	
	@Override
	public Goods findOne(Long goodsId) {
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
	
		Map  categoryMap = new HashMap();
		categoryMap.put("category1", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
		categoryMap.put("category2", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
		categoryMap.put("category3", itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
		goods.setCategoryMap(categoryMap);
		
		goods.setTbGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
		goods.setTbGoodsDesc(tbGoodsDesc);
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goodsId);
		List<TbItem> itemList = itemMapper.selectByExample(example );
		goods.setItemList(itemList);
		return goods;
	}


	@Override
	public List<Goods> findAll() {
		List<Goods> goodsList = new ArrayList<Goods>();
		List<TbGoods> tbGoodsList = goodsMapper.selectByExample(null);
		for (TbGoods tbGoods : tbGoodsList) {
			Goods goods = findOne(tbGoods.getId());
			goodsList.add(goods);
		}
		return goodsList;
	}

}
