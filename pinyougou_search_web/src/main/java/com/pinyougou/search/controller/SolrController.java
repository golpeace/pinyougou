package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SolrService;

@RestController
@RequestMapping("/solr")
public class SolrController {

	
	@Reference
	private SolrService solrService;
	@RequestMapping("/searchFromSolr")
	public Map searchFromSolr(@RequestBody Map paramMap) {
		
		return solrService.searchFromSolr(paramMap);
		
	}
}
