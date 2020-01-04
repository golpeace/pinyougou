app.service("payService",function($http){
	this.createNative=function(){
		return $http.get("./pay/createNative");
	} 
	
	this.queryPayStatus=function(out_trade_no){
		return $http.get("./pay/queryPayStatus/"+out_trade_no);
	}
	
})