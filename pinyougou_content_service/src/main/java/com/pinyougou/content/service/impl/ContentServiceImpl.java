package com.pinyougou.content.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		
//		新增
		contentMapper.insert(content);		
		
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());//同步更新redis缓存数据
		
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
//		需要考虑分类变化的情况  有可能是 轮播广告 转到了今日推荐  涉及到两个分类数据的变化
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();//获取广告未更新时的分类id
		
		contentMapper.updateByPrimaryKey(content);
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());//同步更新redis缓存数据
		
		if(categoryId.longValue()!=content.getCategoryId().longValue()) {
			redisTemplate.boundHashOps("content").delete(categoryId);//同步更新redis缓存数据
		}
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
//		删除
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			contentMapper.deleteByPrimaryKey(id);
			redisTemplate.boundHashOps("content").delete(categoryId);//同步更新redis缓存数据
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Autowired
		private RedisTemplate  redisTemplate;
		
		@Override
		public List<TbContent> findContentByCategoryId(Long cid) {
			List<TbContent> contentList =  (List<TbContent>) redisTemplate.boundHashOps("content").get(cid);
			if(contentList==null) {
				System.out.println("数据是从mysql中获取");
				TbContentExample example = new TbContentExample();
				example.createCriteria().andCategoryIdEqualTo(cid).andStatusEqualTo("1");
				example.setOrderByClause("sort_order");  //用来排序  sortOrder  sort_order
				contentList =  contentMapper.selectByExample(example ); 
				redisTemplate.boundHashOps("content").put(cid, contentList);
			}else {
				System.out.println("数据是从REDIS中获取");
			}
			
			
			return contentList;
		}
	
}
