package entity;

import java.io.Serializable;

public class Result implements Serializable {
//	esponse格式：{success:true|false,message:"添加成功"|"添加失败"}
	
	private boolean  success;
	
	private String message;
	
	public Result(boolean  success,String message) {
		this.success=success;
		this.message=message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
