package com.pinyougou.itempage.service.impl;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.pinyougou.itempage.service.ItemPageService;
import com.pinyougou.pojo.TbItem;

import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;

public class AddItemPageConsumer implements MessageListener{

	@Autowired
	private ItemPageService itemPageService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage message = (TextMessage) arg0;
		
		try {
			String goodsId = message.getText();
			Goods goods = itemPageService.findOne(Long.parseLong(goodsId));
//		生成静态页面
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item112.ftl");
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
			System.out.println("静态页已生成");
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}
