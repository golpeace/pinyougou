package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;

import entity.Result;
import groupEntity.Cart;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Reference
	private CartService cartService;

	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String sessionId = getSessionId();
		List<Cart> cartList_sessionId = cartService.findCartListBySessionId(sessionId);
//		根据sessionId获取的
		
		if(!username.equals("anonymousUser")) {
//			登录
			 List<Cart> cartList_username = cartService.findCartListBySessionId(username);
			 if(cartList_sessionId.size()!=0) {  //如果cartList_sessionId不为空再考略合并
	//			根据用户名获取
				 
//				 合并购物车
				 cartList_username =  cartService.mergeCartList(cartList_sessionId,cartList_username);
				 
	//			 清空 根据sessionId存放的数据
				 cartService.deleteCartListByKey(sessionId);
			 }
//			 合并后的需要再存到redis中
			 cartService.saveCartListByUserName(username, cartList_username);
			 
			 
			 return cartList_username;
		}
		
		return cartList_sessionId;
	}
	
	
	@RequestMapping("/addGoodsToCartList/{itemId}/{num}")
	@CrossOrigin(origins="http://item.pinyougou.com")  //信任http://item.pinyougou.com网站发过来的请求
//	                                                      允许http://item.pinyougou.com来请求我的方法
	public Result addGoodsToCartList(@PathVariable("itemId") Long itemId,@PathVariable("num") int num) {
		List<Cart> cartList = findCartList();//原购物车数据
		try {
			cartList  = cartService.addGoodsToCartList(cartList,itemId,num);
//			存redis中
			String sessionId = getSessionId();
			
			
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			if(!username.equals("anonymousUser")) {
//				登录
				cartService.saveCartListByUserName(username,cartList);
			}else {
//				不登录
				cartService.saveCartListBySessionId(sessionId,cartList);
			}
			
			
			
			return new Result(true, "添加成功");
		} catch (RuntimeException e) {
			return new Result(false, e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
		
	}
	
	private String getSessionId() {
		
//		cookie中获取sessionId
		String sessionId = CookieUtil.getCookieValue(request, "sessionId", "utf-8");
		if(sessionId==null) {
//			如果没有产生一个sessionId存放到Cookie中
			sessionId = session.getId();
			System.out.println("新产生的sessionId："+sessionId);
			CookieUtil.setCookie(request, response, "sessionId", sessionId, 48*60*60, "utf-8");
		}
		
		return sessionId;
		
	}
	
}
