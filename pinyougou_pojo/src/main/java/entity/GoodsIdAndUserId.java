package entity;

import java.io.Serializable;

public class GoodsIdAndUserId  implements Serializable{

	private Long goodsId;
	
	private String userId;

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public GoodsIdAndUserId(Long goodsId, String userId) {
		super();
		this.goodsId = goodsId;
		this.userId = userId;
	}
	
	
}
