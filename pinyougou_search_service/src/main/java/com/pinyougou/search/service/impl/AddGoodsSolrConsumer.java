package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

public class AddGoodsSolrConsumer implements MessageListener{

	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage message = (TextMessage)arg0;
		try {
			String goodsId = message.getText();
			TbItemExample example = new TbItemExample();
			example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
			List<TbItem> itemList = itemMapper.selectByExample(example);
			solrTemplate.saveBeans(itemList);
			solrTemplate.commit();
			System.out.println("已同步完成solr:"+goodsId);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
