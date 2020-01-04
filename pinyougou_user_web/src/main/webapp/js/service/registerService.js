app.service("registerService",function($http){
	this.sendCode=function(phone){
		return $http.get("./user/sendCode/"+phone);
	}
	
	this.register=function(user,code){
		return $http.post("./user/add/"+code,user);
	}
	
})