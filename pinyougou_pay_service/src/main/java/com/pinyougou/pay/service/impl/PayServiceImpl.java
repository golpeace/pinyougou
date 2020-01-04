package com.pinyougou.pay.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

import util.HttpClient;
import util.IdWorker;

@Service
public class PayServiceImpl implements PayService {
	@Value("${appid}")
	private  String appid;
	@Value("${partner}")
	private String  partner;
	@Value("${partnerkey}")
	private String  partnerkey;
	@Value("${notifyurl}")
	private String  notifyurl;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map createNative(String userId) throws Exception {
		
		
	    TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	    String out_trade_no = payLog.getOutTradeNo();
//		long out_trade_no = idWorker.nextId();
		
		HttpClient  httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
//		调用微信的统一下单API
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", appid); //公众账号ID
		paramMap.put("mch_id", partner); //商户号
		paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
		paramMap.put("body", "品优购支付"); //商品描述
		paramMap.put("out_trade_no", out_trade_no); //商户订单号
		paramMap.put("total_fee", "1"); //标价金额
		paramMap.put("spbill_create_ip", "127.0.0.1"); //终端IP
		paramMap.put("notify_url", notifyurl); //通知地址
		paramMap.put("trade_type", "NATIVE"); //交易类型  扫码支付
		
//		把paramMap转成xml  转xml时带有签名
		String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//转xml时带有签名
//		给HTTPClient设置参数
		httpClient.setXmlParam(paramXml);
//		发送请求
		httpClient.post();
//		获取返回的结果  是xml
		String content = httpClient.getContent();
//		把返回的结果转成map
		Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
		
		resultMap.put("out_trade_no", out_trade_no);
		resultMap.put("total_fee", payLog.getTotalFee()+""); //需要支付的金额
		
		System.out.println(resultMap);
		return resultMap;
	}

	@Override
	public Map  queryPayStatus(String out_trade_no) {
		HttpClient  httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
//		调用微信的查询订单支付状态API
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", appid); //公众账号ID
		paramMap.put("mch_id", partner); //商户号
		paramMap.put("out_trade_no", out_trade_no); //商户的订单号
		paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
//		把paramMap转成xml  转xml时带有签名
		try {
			String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
			
			httpClient.setXmlParam(paramXml);
			httpClient.post();
			
			String content = httpClient.getContent();
			
			Map<String, String> resultMap = WXPayUtil.xmlToMap(content);	
			
			return resultMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}//转xml时带有签名
	}

	@Override
	public void updatePayLogAndOrder(String transaction_id, String userId) {
		 TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		 payLog.setPayTime(new Date());
		 payLog.setTradeState("1");
		 payLog.setTransactionId(transaction_id);
		 payLogMapper.updateByPrimaryKey(payLog);
		 
		 String[] orderIds = payLog.getOrderList().split(",");
		 for (String orderId : orderIds) {
			 TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			 order.setPaymentTime(new Date());
			 order.setStatus("2");
			 order.setUpdateTime(new Date());
			 orderMapper.updateByPrimaryKey(order);
		}
		 
//		 支付成功后 订单的状态已改  可以从redis中移除 支付日志对象
		 redisTemplate.boundHashOps("payLog").delete(userId);
		 
	}
	
}
