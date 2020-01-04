package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;

import groupEntity.Cart;

@Service
public class CartServiceImpl implements CartService {

	
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	@Override
	public List<Cart> findCartListBySessionId(String sessionId) {
		
		String str = (String) redisTemplate.boundValueOps(sessionId).get();
		if(str==null) {
			str="[]";    //对于集合的空值处理
		}
		return JSON.parseArray(str, Cart.class);
	}
	@Override
	public List<Cart> findCartListByUsername(String username) {
		
		String str = (String) redisTemplate.boundValueOps(username).get();
		if(str==null) {
			str="[]";    //对于集合的空值处理
		}
		return JSON.parseArray(str, Cart.class);
	}
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		String sellerId = tbItem.getSellerId();
//		 原购物车数据中是否有此商家，
		Cart cart = findCartFromCartList(cartList, sellerId);
		if(cart!=null) { // 有此商家
//			还需要判断是否有此商品
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
//			List<TbOrderItem> orderItemList = orderItemList;
			TbOrderItem orderItem = findOrderItemFromOrderItemList(orderItemList, itemId);
			if(orderItem!=null) { //有此商品
//				数量累加 
				orderItem.setNum(orderItem.getNum()+num);
				if(orderItem.getNum()<=0) { //从购物车列表中减商品
					orderItemList.remove(orderItem);
					
					if(orderItemList.size()<=0) {   //一个购物车对象中没有orderItemList表示此商家已没有商品 把此商家对象的购物车对象移除
						cartList.remove(cart);
					}
					
					
				}
				
				
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
			}else {//没有此商品
//				向当前购物车对象中的orderItemList中追加商品
				orderItem = createTbOrderItem(tbItem, num);
				orderItemList.add(orderItem);//向当前购物车对象中的orderItemList中追加商品
				
			}
		}else {//没有此商家
//			直接new Cart() 再向购物车对象中的orderItemList中追加商品
			cart = new  Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(tbItem.getSeller());
			List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
			TbOrderItem orderItem = createTbOrderItem(tbItem, num);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			
			cartList.add(cart);  //把新的cart对象放到购物车列表中
		}
		
//		把新构建的购物车列表数据存放到redis中
		return cartList;
		
		
	}
	
	private Cart findCartFromCartList(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	private TbOrderItem  findOrderItemFromOrderItemList(List<TbOrderItem> orderItemList,Long itemId ) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if(tbOrderItem.getItemId().longValue()==itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}

	
	private TbOrderItem createTbOrderItem(TbItem tbItem,int num) {
		
		if(num<1) {
			throw new RuntimeException("数量非法");
		}
		
		TbOrderItem orderItem=new TbOrderItem();
		orderItem.setGoodsId(tbItem.getGoodsId());
		 
		orderItem.setItemId(tbItem.getId());
		orderItem.setNum(num);
		
		orderItem.setPicPath(tbItem.getImage());
		orderItem.setPrice(tbItem.getPrice());
		orderItem.setSellerId(tbItem.getSellerId());
		orderItem.setTitle(tbItem.getTitle());
		orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
		
		return orderItem;
	}
	
	@Override
	public void saveCartListBySessionId(String sessionId, List<Cart> cartList) {
		redisTemplate.boundValueOps(sessionId).set(JSON.toJSONString(cartList), 2, TimeUnit.DAYS);
		
	}
	@Override
	public void saveCartListByUserName(String username, List<Cart> cartList) {
		redisTemplate.boundValueOps(username).set(JSON.toJSONString(cartList), 30, TimeUnit.DAYS);
	}
	
//	把cartList_sessionId合并到cartList_username上
	@Override
	public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
//		cartList_sessionId合并到cartList_username上

		for (Cart cart : cartList_sessionId) {
			for(TbOrderItem orderItem:cart.getOrderItemList()) {
				cartList_username = addGoodsToCartList(cartList_username, orderItem.getItemId(), orderItem.getNum());
			}
		}
		
		return cartList_username;
	}
	@Override
	public void deleteCartListByKey(String sessionId) {
		redisTemplate.delete(sessionId);
		
	}
}
