package com.pinyougou.user.service.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import util.HttpClient;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public void sendCode(String phone) throws Exception{
		String randomNumeric = RandomStringUtils.randomNumeric(6);
		HttpClient httpClient = new HttpClient("http://localhost:7788/sms/sendSms");
		httpClient.addParameter("phoneNumbers", phone);
		httpClient.addParameter("signName", "品位优雅购物");
		httpClient.addParameter("templateCode", "SMS_130926832");
		httpClient.addParameter("templateParam", "{\"code\":\""+randomNumeric+"\"}");
		httpClient.post();
		String content = httpClient.getContent();
		System.out.println(content);
//		HTTPClient发送短信
//		验证码存到redis中
		redisTemplate.boundValueOps(phone).set(randomNumeric, 5, TimeUnit.MINUTES);
	}

	@Override
	public void add(String code, TbUser tbUser) {
//		code：页面上
		String smsCode = (String) redisTemplate.boundValueOps(tbUser.getPhone()).get();
		if(smsCode==null) {
			throw new RuntimeException("验证码失效");
		}
		if(!smsCode.equals(code)) {
			throw new RuntimeException("验证码错误");
		}
		tbUser.setCreated(new Date());
		tbUser.setUpdated(new Date());
		tbUser.setSourceType("1");
		tbUser.setStatus("Y");
//		`created` datetime NOT NULL COMMENT '创建时间',
//		  `updated` datetime NOT NULL,
//		  `source_type` varchar(1) DEFAULT NULL COMMENT '会员来源：1:PC，2：H5，3：Android，4：IOS，5：WeChat',
		
		String password = tbUser.getPassword();
		password = DigestUtils.md5Hex(password);  //0123456789abcdef
		tbUser.setPassword(password);
		userMapper.insert(tbUser);
		
		redisTemplate.delete(tbUser.getPhone());
		
		
	}

}
