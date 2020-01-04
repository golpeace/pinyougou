package com.pinyougou.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class SolrManager {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Test
	public void testAddAll() {
//		查询的数据：所有已上架的sku数据
//		sql:  select i.* from tb_goods g,tb_item i where g.id=i.goods_id and g.is_marketable='1'
		List<TbItem> tbItemList= itemMapper.selectMarket();
		
		for (TbItem tbItem : tbItemList) {
			//{"机身内存":"16G","网络":"联通3G"}
			tbItem.setSpecMap(JSON.parseObject( tbItem.getSpec(), Map.class));
		}
		
		
		solrTemplate.saveBeans(tbItemList);
		solrTemplate.commit();
	}
	
	
	@Test
	public void testAdd() {
		TbItem tbItem = new TbItem();
		tbItem.setId(4l);
		tbItem.setGoodsId(12121212l);
		tbItem.setTitle("商品测试");
//		<field name="item_goodsid" type="long" indexed="true" stored="true"/>
//		<field name="item_title" type="text_ik" indexed="true" stored="true"/>
//		<field name="item_price" type="double" indexed="true" stored="true"/>
//		<field name="item_image" type="string" indexed="false" stored="true" />
//		<field name="item_category" type="string" indexed="true" stored="true" />
//		<field name="item_seller" type="text_ik" indexed="true" stored="true" />
//		<field name="item_brand" type="string" indexed="true" stored="true" />
		solrTemplate.saveBean(tbItem);
//		solrTemplate.saveBeans(beans);
		solrTemplate.commit();
	}
	
	@Test
	public void testUpdate() {
		TbItem tbItem = new TbItem();
		tbItem.setId(3l);
		tbItem.setGoodsId(333333333l);
		tbItem.setTitle("商品测试修改");
		
		solrTemplate.saveBean(tbItem); //saveOrUpdate id
//		solrTemplate.saveBeans(beans);
		solrTemplate.commit();
	}
	
	@Test
	public void testQueryById() {
//		TbItem tbItem = solrTemplate.getById("4", TbItem.class);
//		System.out.println(tbItem.getTitle());
		List<String> ids = new ArrayList<>();
		ids.add("3");
		ids.add("4");
		Collection<TbItem> tbItemList = solrTemplate.getById(ids, TbItem.class);
		for (TbItem tbItem2 : tbItemList) {
			System.out.println(tbItem2.getTitle());
			
		}
		
	}
	
	@Test
	public void testDelete() {
//		solrTemplate.deleteById("1");
		
		SolrDataQuery query = new SimpleQuery("item_goodsid:149187842867967");
		solrTemplate.delete(query );
		
		solrTemplate.commit();
	}
	
	@Test
	public void testQuery() {
//		Query query = new SimpleQuery("item_title:小米") ;
		
//		Query query = new SimpleQuery() ;
//		Criteria arg0 = new Criteria("item_title").is("小米");
//		query.addCriteria(arg0 );
//		
//		ScoredPage<TbItem> queryForPage = solrTemplate.queryForPage(query , TbItem.class);
//		List<TbItem> content = queryForPage.getContent();
//		System.out.println("符合条件的总数："+queryForPage.getTotalElements());
//		for (TbItem tbItem : content) {
//			System.out.println(tbItem.getTitle());
//		}
		
		//设置主查询
		HighlightQuery query = new SimpleHighlightQuery();
		query.addCriteria(new Criteria("item_title").is("小米"));
		query.setRows(2);//每页显示的条数

		//		高亮的属性：高亮哪个域   使用什么html标签高亮
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.addField("item_title");
		highlightOptions.setSimplePrefix("<span style=\"color:red\">");
		highlightOptions.setSimplePostfix("</span>");
		query.setHighlightOptions(highlightOptions);
		
//		实现查询
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query , TbItem.class);
		
		
		System.out.println(JSON.toJSONString(highlightPage, true));
		
		List<TbItem> content = highlightPage.getContent();
		for (TbItem tbItem : content) {
//			根据实体类查询高亮的数据
			List<Highlight> highlights = highlightPage.getHighlights(tbItem);
			List<String> snipplets = highlights.get(0).getSnipplets();
			tbItem.setTitle(snipplets.get(0));
			
			System.out.println(tbItem.getTitle());
		}
		
	}
	
	


}
