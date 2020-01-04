package com.pinyougou.data;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class RedisManager {

	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	@Test
	public void initRedis() {
		List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
		for (TbItemCat tbItemCat : itemCatList) {
			TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(tbItemCat.getTypeId());
			String brandIds = typeTemplate.getBrandIds();//[{id:11,text:xxx},{},{}]
			List<Map> brandList = JSON.parseArray(brandIds, Map.class);
//			分类和品牌
			redisTemplate.boundHashOps("itemCat_brand").put(tbItemCat.getName(),brandList);
			
			String specIds = typeTemplate.getSpecIds();//[{"id":33,"text":"电视屏幕尺寸",options:[{},{}]}]
			List<Map> specMapList = JSON.parseArray(specIds, Map.class);
			for (Map map : specMapList) {
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				example.createCriteria().andSpecIdEqualTo(Long.parseLong(map.get("id")+""));
				List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example );
				map.put("options", specificationOptions);
			}
//			分类和规格
			redisTemplate.boundHashOps("itemCat_spec").put(tbItemCat.getName(),specMapList);
		}

		

		System.out.println("数据初始化完成");
		
		
	}
}
