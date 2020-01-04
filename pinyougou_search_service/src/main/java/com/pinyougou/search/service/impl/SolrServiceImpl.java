package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SolrService;

@Service
public class SolrServiceImpl implements SolrService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map searchFromSolr(Map paramMap) {
		Map resultMap = new HashMap<>();
		
		//分类分组查询开始-------------------
		Query groupQuery = new SimpleQuery();
		groupQuery.addCriteria(new Criteria("item_keywords").is(paramMap.get("keyword"))); //设置主条件
		
		//设置分组的属性
		GroupOptions groupOption = new GroupOptions(); 
		//		select category from tb_item group by category
		groupOption.addGroupByField("item_category");
		groupQuery.setGroupOptions(groupOption);
		
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery , TbItem.class);
		
		//获取分组的结果
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>>  groupEntrieList = groupEntries.getContent();
		List<String> categoryList = new ArrayList<>();
		for (GroupEntry<TbItem> groupEntry : groupEntrieList) {
			categoryList.add(groupEntry.getGroupValue());
		}
		resultMap.put("categoryList", categoryList);
		//分类分组查询结束-------------------

		
		if(categoryList.size()>0) {
//			根据第一个分类查询品牌数据
			List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("itemCat_brand").get(categoryList.get(0));
			resultMap.put("brandList", brandList);
//			根据第一个分类查询规格数据
			
			List<Map> specList = (List<Map>) redisTemplate.boundHashOps("itemCat_spec").get(categoryList.get(0));
			resultMap.put("specList", specList);
		}

		
		//设置主查询
		HighlightQuery query = new SimpleHighlightQuery();
		query.addCriteria(new Criteria("item_keywords").is(paramMap.get("keyword")));
//		query.setRows(2);//每页显示的条数
		
		
//		过滤查询
//		分类
		if(!paramMap.get("category").equals("")) {
			FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category")));
			query.addFilterQuery(filterQuery );
		}
		
		
//		品牌
		if(!paramMap.get("brand").equals("")) {
			FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand")));
			query.addFilterQuery(filterQuery );
		}
		
//		规格
		Map<String,String> specMap = (Map) paramMap.get("spec"); //"spec":{屏幕尺寸:4.5寸,机身内存:16G}};
		for(String key:specMap.keySet()) {
			FilterQuery filterQuery = new SimpleFilterQuery(new Criteria("item_spec_"+key).is(specMap.get(key)));
			query.addFilterQuery(filterQuery );
		}
		
		
//		价格
//		'0-500' 500-1000 3000-*
		if(!paramMap.get("price").equals("")) {
			String[] prices = (paramMap.get("price")+"").split("-");
			FilterQuery filterQuery = null;
			if(prices[1].equals("*")) {
				filterQuery = new SimpleFilterQuery
						(new Criteria("item_price").greaterThanEqual(prices[0]));
			}else {
				filterQuery = new SimpleFilterQuery
						(new Criteria("item_price").between(prices[0], prices[1], true, true));
			}
			query.addFilterQuery(filterQuery );
		}
		
//		价格排序
		if(paramMap.get("order").equals("asc")) {
//			升序
			query.addSort(new Sort(Direction.ASC, "item_price"));
		}else {
//			降序
			query.addSort(new Sort(Direction.DESC, "item_price"));
		}
		
		
		//分页
		int page = Integer.parseInt(paramMap.get("page")+"") ;
		query.setOffset((page-1)*60); //0  60 120    180  240   起始位置 和mysql一样   limit start,rows
		query.setRows(60);//每页显示的条数
		
		

		//		高亮的属性：高亮哪个域   使用什么html标签高亮
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.addField("item_title");
		highlightOptions.setSimplePrefix("<span style=\"color:red\">");
		highlightOptions.setSimplePostfix("</span>");
		query.setHighlightOptions(highlightOptions);
		
//				实现查询
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query , TbItem.class);
		
		
		System.out.println(JSON.toJSONString(highlightPage, true));
		
		List<TbItem> content = highlightPage.getContent();
		for (TbItem tbItem : content) {
//					根据实体类查询高亮的数据
			List<Highlight> highlights = highlightPage.getHighlights(tbItem);
			if(highlights.size()>0) {
				List<String> snipplets = highlights.get(0).getSnipplets();
				if(snipplets.size()>0) {
					tbItem.setTitle(snipplets.get(0));
				}
			}
			
			
			
			System.out.println(tbItem.getTitle());
		}
		
		resultMap.put("total", highlightPage.getTotalElements());
		resultMap.put("totalPages", highlightPage.getTotalPages());
		
		resultMap.put("itemList", content);
		
		
		
		
		return resultMap;
	}

}
