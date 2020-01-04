package com.pinyougou.cart.service;

import java.util.List;

import groupEntity.Cart;

public interface CartService {

	List<Cart> findCartListBySessionId(String sessionId);
	
	List<Cart> findCartListByUsername(String username);

	List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num);

	void saveCartListBySessionId(String sessionId, List<Cart> cartList);

	void saveCartListByUserName(String username, List<Cart> cartList);

	List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username);

	void deleteCartListByKey(String sessionId);

}
