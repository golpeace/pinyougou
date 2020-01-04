contentapp.service("brandService",function($http){
	
//	  分页查询
	  this.findByPage=function(pageNum,pageSize){
		 return $http.get("../brand/findPage/"+pageNum+"/"+pageSize);
	  }
	  
	  this.findAll=function(){
		  return $http.get("../brand/findAll");
	  }
	  
	  this.findBrandList=function(){
		  return $http.get("../brand/findBrandList");
	  }
	  
	  
	  this.add=function(entity){
		return  $http.post("../brand/add",entity);
	  }
	  
	  this.update=function(entity){
		return  $http.post("../brand/update",entity);
	  }
	  
	  
//	  根据id查询对象
	  this.findOne=function(id){
		  return $http.get("../brand/findOne/"+id);

	  }
	  
	 

	  this.dele=function(selectIds){
			return  $http.get("../brand/dele/"+selectIds);
	  }
	  
	  
	  this.search=function(pageNum,pageSize,searchEntity){
		  return $http.post("../brand/search/"+pageNum+"/"+pageSize,searchEntity);
	  }
	
})