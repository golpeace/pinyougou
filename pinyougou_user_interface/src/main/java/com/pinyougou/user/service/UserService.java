package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {

	void sendCode(String phone) throws Exception;

	void add(String code, TbUser tbUser);

}
