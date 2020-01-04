package com.pinyougou.freemarker.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.itempage.service.ItemPageService;
import com.pinyougou.pojo.TbItem;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import groupEntity.Goods;

@RestController
@RequestMapping("/itempage")
public class ItemPageController {
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Reference
	private ItemPageService itemPageService;
	
	@RequestMapping("/generatorToHtmlAll")
	public String generatorToHtmlAll() {
		try {
			List<Goods> goodsList = itemPageService.findAll();
			
			for (Goods goods : goodsList) {
	//		生成静态页面
				Configuration configuration = freeMarkerConfigurer.getConfiguration();
				Template template = configuration.getTemplate("item.ftl");
				
	//		和数据
				List<TbItem> itemList = goods.getItemList();
				for (TbItem tbItem : itemList) {
					
					Map map = new HashMap();
					map.put("goods", goods);
					map.put("tbItem", tbItem);
					FileWriter writer = new FileWriter("d://class47//html//"+tbItem.getId()+".html");
					template.process(map, writer);
					writer.close();
				}
			}
			return "SUCCESS";
			
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
	@RequestMapping("/generatorToHtml")
	public String generatorToHtml(Long goodsId) {
		try {
			Goods goods = itemPageService.findOne(goodsId);
//		生成静态页面
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			
//		和数据
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				
				Map map = new HashMap();
				map.put("goods", goods);
				map.put("tbItem", tbItem);
				FileWriter writer = new FileWriter("d://class47//html//"+tbItem.getId()+".html");
				template.process(map, writer);
				writer.close();
			}
			
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
				
	}

}
