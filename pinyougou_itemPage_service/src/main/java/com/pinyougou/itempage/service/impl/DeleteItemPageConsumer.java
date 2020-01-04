package com.pinyougou.itempage.service.impl;

import java.io.File;
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
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;

public class DeleteItemPageConsumer implements MessageListener{

	@Autowired
	private TbItemMapper itemMapper;
	
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage message = (TextMessage) arg0;
		try {
			String goodsId = message.getText();
			TbItemExample example = new TbItemExample();
			example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
			List<TbItem> itemList = itemMapper.selectByExample(example);
			for (TbItem tbItem : itemList) {
				new File("D:\\class47\\html\\"+tbItem.getId()+".html").delete();
			}
			System.out.println("静态页同步删除");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
