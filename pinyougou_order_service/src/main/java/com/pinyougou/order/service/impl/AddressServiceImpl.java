package com.pinyougou.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.order.service.AddressService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
	private TbAddressMapper addressMapper;
	@Override
	public List<TbAddress> findAddressByUser(String username) {
		TbAddressExample example = new TbAddressExample();
		example.createCriteria().andUserIdEqualTo(username);
		return addressMapper.selectByExample(example );
	}

}
