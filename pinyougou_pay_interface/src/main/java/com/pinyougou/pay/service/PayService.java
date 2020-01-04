package com.pinyougou.pay.service;

import java.util.Map;

public interface PayService {

	Map createNative(String userId) throws Exception;

	Map queryPayStatus(String out_trade_no);

	void updatePayLogAndOrder(String transaction_id, String userId);

}
