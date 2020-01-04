app.service("specificationService",function($http){
	
//	  分页查询
	  this.findByPage=function(pageNum,pageSize){
		 return $http.get("../specification/findPage/"+pageNum+"/"+pageSize);
	  }
	  
	  this.findAll=function(){
		  return $http.get("../specification/findAll");
	  }
	  this.findSpecList=function(){
		  return $http.get("../specification/findSpecList");
	  }
	  
	  
	  this.add=function(entity){
		return  $http.post("../specification/add",entity);
	  }
	  
	  this.update=function(entity){
		return  $http.post("../specification/update",entity);
	  }
	  
	  
//	  根据id查询对象
	  this.findOne=function(id){
		  return $http.get("../specification/findOne/"+id);

	  }
	  
	 

	  this.dele=function(selectIds){
			return  $http.get("../specification/dele/"+selectIds);
	  }
	  
	  
	  this.search=function(pageNum,pageSize,searchEntity){
		  return $http.post("../specification/search/"+pageNum+"/"+pageSize,searchEntity);
	  }
	
})