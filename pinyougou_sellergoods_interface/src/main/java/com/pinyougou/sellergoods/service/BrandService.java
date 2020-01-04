package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {

	public  List<TbBrand>  findAll();

	public PageResult findPage(int pageNum,int pageSize);

	public void add(TbBrand brand);

	public TbBrand findOne(Long id);

	public void update(TbBrand brand);

	public void dele(Long[] ids);

	public PageResult search(int pageNum, int pageSize, TbBrand brand);

	public List<Map> findBrandList();
}
