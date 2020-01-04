package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import groupEntity.Goods;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");
		tbGoods.setIsMarketable("0");
		goodsMapper.insert(tbGoods);		
		
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(tbGoodsDesc);
		
		
		if(tbGoods.getIsEnableSpec().equals("1")) {  //启用规格
//			保存sku数据
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				String title = tbGoods.getGoodsName();
//				title="小米6X 32G 双卡 5寸 ";
//				小米6X{机身内存: "32G", 网络制式: "双卡", 屏幕尺寸: "5寸"}
				Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
				for(String key:specMap.keySet()) {
					title +=" "+specMap.get(key);
				}
//				  `title` varchar(100) NOT NULL COMMENT '商品标题',  小米6X 32G双卡5.5寸
				tbItem.setTitle(title);
				tbItem = createTbItem(tbItem, tbGoodsDesc, tbGoods);
				
				itemMapper.insert(tbItem);
			}
		}else {  //不启用规格  title和商品名称一致  保存一个tbItem对象
			TbItem tbItem = new TbItem();
//			价格	库存	是否启用	是否默认
			tbItem.setPrice(tbGoods.getPrice());
			tbItem.setNum(9999);
			tbItem.setStatus("1");
			tbItem.setIsDefault("0");
			
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem = createTbItem(tbItem, tbGoodsDesc, tbGoods);
			
			itemMapper.insert(tbItem);
		}

		
		
		
	}
	
	private TbItem createTbItem(TbItem tbItem,TbGoodsDesc tbGoodsDesc,TbGoods tbGoods) {
//		  `image` varchar(2000) DEFAULT NULL COMMENT '商品图片', 保存spu中第一个图片
		String itemImages = tbGoodsDesc.getItemImages();
		
//		[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}
//		,{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWs2ABppQAAETwD7A1Is142.jpg"}]
		List<Map> itemImageList = JSON.parseArray(itemImages, Map.class);
		if(itemImageList.size()>0) {
			tbItem.setImage(itemImageList.get(0).get("url")+"");
		}
//		  `categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  三级分类id
		tbItem.setCategoryid(tbGoods.getCategory3Id());
//		  `create_time` datetime NOT NULL COMMENT '创建时间',
		tbItem.setCreateTime(new Date());
//		  `update_time` datetime NOT NULL COMMENT '更新时间',
		tbItem.setUpdateTime(new Date());
		
//		  `goods_id` bigint(20) DEFAULT NULL,  商品id
		tbItem.setGoodsId(tbGoods.getId());
//		  `seller_id` varchar(30) DEFAULT NULL,  商家id
		tbItem.setSellerId(tbGoods.getSellerId());
//		  `category` varchar(200) DEFAULT NULL, 分类名称
		tbItem.setCategory(itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
//		  `brand` varchar(100) DEFAULT NULL,  品牌名称
		tbItem.setBrand(brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName());
//		  `seller` varchar(200) DEFAULT NULL  商家名称
		tbItem.setSeller(sellerMapper.selectByPrimaryKey(tbGoods.getSellerId()).getName());
		
		return tbItem;
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void updateAuditStatus(String auditStatus, Long[] ids) {
			//update tb_goods set  audit_Status=? where id=?
			for (Long id : ids) {
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
				tbGoods.setAuditStatus(auditStatus);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}
			
		}

		@Autowired
		private JmsTemplate jmsTemplate;
		@Autowired
		@Qualifier("queueGoodsAdd")
		private Destination queueGoodsAdd;
		@Autowired
		@Qualifier("queueGoodsDelete")
		private Destination queueGoodsDelete;
		/**
		 * 商品上下架  isMarketable 1 上架 2 下架
		 */
		@Override
		public void updateIsMarketable(String isMarketable, Long[] ids) {
			
//			if(isMarketable.equals("1")) {
////				同步更新solr索引库
////				同步更新商品静态页
//			}
			
			for (Long id : ids) {
				if(isMarketable.equals("1")) { //上架后把需要上架的商品id放到MQ中 让solr同步添加
					jmsTemplate.send(queueGoodsAdd, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage createTextMessage = session.createTextMessage(id+"");
							return createTextMessage;
						}
					});
				}
				if(isMarketable.equals("2")) { //下架后把需要下架的商品id放到MQ中 让solr同步删除
					jmsTemplate.send(queueGoodsDelete, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage createTextMessage = session.createTextMessage(id+"");
							return createTextMessage;
						}
					});
				}
				
				
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
				tbGoods.setIsMarketable(isMarketable);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}
			
		}
	
}
