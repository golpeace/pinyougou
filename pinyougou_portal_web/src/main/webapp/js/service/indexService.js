app.service("indexService",function($http){
	this.findContentByCategoryId=function(categoryId){
		
		return $http.get("./content/findContentByCategoryId/"+categoryId);
		
	}
	
})