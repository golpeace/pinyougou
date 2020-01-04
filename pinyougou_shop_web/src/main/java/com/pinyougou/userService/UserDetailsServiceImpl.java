package com.pinyougou.userService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {

	
	private SellerService  sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TbSeller tbSeller =  sellerService.findBySellerId(username);
		
		if(tbSeller==null) {
			return null;
		}
		//判断是否审核通过的
		if(!tbSeller.getStatus().equals("1")) {
			return null;
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority authoritie = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(authoritie);
		return new User(username, tbSeller.getPassword(), authorities );
	}

}
