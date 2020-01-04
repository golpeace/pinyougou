package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

//@Controller
//@ResponseBody  //把java对象装成json 回显到浏览器上
@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService brandService;
	
//	查询符合模板数据格式的品牌列表
//	[{id:1,text:"联想"},{id:2,text:"小米"}]
	@RequestMapping("/findBrandList")
	public List<Map> findBrandList(){
		return brandService.findBrandList();
	}
	
	
	@RequestMapping("/add")   //@RequestBody接受json对象
	public Result add(@RequestBody TbBrand brand){
		try {
			brandService.add(brand);
			return new Result(true,"新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"新增失败");
		}
	}
	
	@RequestMapping("/dele/{ids}")   //@RequestBody接受json对象
	public Result dele(@PathVariable("ids") Long[] ids){
		try {
			brandService.dele(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	@RequestMapping("/update")   //@RequestBody接受json对象
	public Result update(@RequestBody TbBrand brand){
		try {
			brandService.update(brand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("/findOne/{id}")    
	public TbBrand findOne(@PathVariable("id") Long id){
		 return brandService.findOne(id);
	}
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	
	
	@RequestMapping("/findPage/{pageNum}/{pageSize}")
//	@PathVariable 从url地址中获取变量值
	public PageResult findPage(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize")int pageSize ){
//		{total：100,rows：[{},{},{]}]} 
		return brandService.findPage(pageNum,pageSize);
	}
	
	@RequestMapping("/search/{pageNum}/{pageSize}")
//	@PathVariable 从url地址中获取变量值
	public PageResult search(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize")int pageSize,@RequestBody TbBrand brand ){
//		{total：100,rows：[{},{},{]}]} 
		return brandService.search(pageNum,pageSize,brand);
	}
	
	

}
