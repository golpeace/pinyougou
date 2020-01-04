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
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import entity.Result;
import groupEntity.Specification;

//@Controller
//@ResponseBody  //把java对象装成json 回显到浏览器上
@RestController
@RequestMapping("/specification")
public class SpecificationController {
	
	@Reference
	private SpecificationService specificationService;
	
	
//	查询符合新增模板时需要的规格数据格式 [{id:1,text:""}]
	@RequestMapping("/findSpecList")
	public List<Map> findSpecList(){
		return specificationService.findSpecList();
	}
	
	@RequestMapping("/add")   //@RequestBody接受json对象
	public Result add(@RequestBody Specification specification){
		try {
			specificationService.add(specification);
			return new Result(true,"新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"新增失败");
		}
	}
	
	@RequestMapping("/dele/{ids}")   //@RequestBody接受json对象
	public Result dele(@PathVariable("ids") Long[] ids){
		try {
			specificationService.dele(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	@RequestMapping("/update")   //@RequestBody接受json对象
	public Result update(@RequestBody Specification specification){
		try {
			specificationService.update(specification);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("/findOne/{id}")    
	public Specification findOne(@PathVariable("id") Long id){
		 return specificationService.findOne(id);
	}
	
	@RequestMapping("/findAll")
	public List<TbSpecification> findAll(){
		return specificationService.findAll();
	}

	
	
	
	@RequestMapping("/findPage/{pageNum}/{pageSize}")
//	@PathVariable 从url地址中获取变量值
	public PageResult findPage(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize")int pageSize ){
//		{total：100,rows：[{},{},{]}]} 
		return specificationService.findPage(pageNum,pageSize);
	}
	
	@RequestMapping("/search/{pageNum}/{pageSize}")
//	@PathVariable 从url地址中获取变量值
	public PageResult search(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize")int pageSize,@RequestBody TbSpecification specification ){
//		{total：100,rows：[{},{},{]}]} 
		return specificationService.search(pageNum,pageSize,specification);
	}
	
	

}
