itemCatapp.controller("brandController",function($scope,$controller,brandService){
  
	$controller('baseController',{$scope:$scope});//继承 本质：共用一个$scope
	
//	  分页查询
	  $scope.findByPage=function(pageNum,pageSize){
		  brandService.findByPage(pageNum,pageSize).success(function(response){
//			response = {total：100,rows：[{},{},{]}]} 
			 $scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		  }) 
		  
	  }
	  
	  $scope.findAll=function(){
		  brandService.findAll().success(function(response){
			  $scope.list = response;
		  })
	  }
	  
	  
	  $scope.save=function(){
		  var resultObj;
		  if($scope.entity.id!=null){
			  resultObj = brandService.update($scope.entity);
		  }else{
			  resultObj = brandService.add($scope.entity);
		  }
		  
		  resultObj.success(function(response){
//			  response格式：{success:true|false,message:"添加成功"|"添加失败"}
			  if(response.success){
				  $scope.reloadList(); //刷新列表数据
			  }else{
				  alert(response.message);
			  }
			  
		  })
		     //JSON.stringify 把对象转成字符串  纯js的方法
//		  alert(JSON.stringify($scope.entity));
	  }
	  
//	  根据id查询对象
	  $scope.findOne=function(id){
		  brandService.findOne(id).success(function(response){
			  $scope.entity=response;
		  })
	  }
	  
	 

	  $scope.dele=function(){
		  if($scope.selectIds.length==0){
			  return ;
		  }
		  
		  var flag = window.confirm("确认要删除您选择的数据吗？");
		  
		  if(flag){
			  brandService.dele($scope.selectIds).success(function(response){
				  if(response.success){
					  $scope.reloadList(); //刷新列表数据 
					  $scope.selectIds=[];
				  }else{
					  alert(response.message);
				  }
			  })
		  }
	  }
	  
	
	  
	  $scope.search=function(pageNum,pageSize){
		  brandService.search(pageNum,pageSize,$scope.searchEntity).success(function(response){
			 $scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		  }) 
		  
	  }
	  $scope.hasId=function(id){
		  for (var i = 0; i < $scope.selectIds.length; i++) {
			 if($scope.selectIds[i]==id){
				 return true;
			 }
		}
		  return false;
	  }
})