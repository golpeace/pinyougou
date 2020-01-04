app.service("seckillGoodsService",function($http){
	this.findAll=function(){
		return $http.get("./seckillGoods/findAllFromRedis");
	} 
	
	
	this.findOne=function(id){
		return $http.get("./seckillGoods/findOneFromRedis/"+id);
	} 
	
	
	
	this.saveOrder=function(id){
		return $http.get("./seckillGoods/saveOrder/"+id);
	} 
	
	
})