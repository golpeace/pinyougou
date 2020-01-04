package com.pinyougou.itempage.service;

import java.util.List;

import groupEntity.Goods;

public interface ItemPageService {

	Goods findOne(Long goodsId);

	List<Goods> findAll();

}
