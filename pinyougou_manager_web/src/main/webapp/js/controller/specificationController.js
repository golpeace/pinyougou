app.controller("specificationController",function($scope,$controller,specificationService){
  
	$controller('baseController',{$scope:$scope});//继承 本质：共用一个$scope
	
	$scope.entity={specificationOptionList:[]};//初始化 entity对象
	
//	动态添加规格项
	$scope.addSpecificationOptionList=function(){
		$scope.entity.specificationOptionList.push({});
	}
//	动态删除规格项
	$scope.deleSpecificationOptionList=function(index){
//		var index = $scope.entity.specificationOptionList.indexOf(pojo);
		$scope.entity.specificationOptionList.splice(index,1);
	}
	
//	  分页查询
	  $scope.findByPage=function(pageNum,pageSize){
		  specificationService.findByPage(pageNum,pageSize).success(function(response){
//			response = {total：100,rows：[{},{},{]}]} 
			 $scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		  }) 
		  
	  }
	  
	  $scope.findAll=function(){
		  specificationService.findAll().success(function(response){
			  $scope.list = response;
		  })
	  }
	  
	  
	  $scope.save=function(){
		  var resultObj;
		  if($scope.entity.tbSpecification.id!=null){
			  resultObj = specificationService.update($scope.entity);
		  }else{
			  resultObj = specificationService.add($scope.entity);
		  }
		  
		  resultObj.success(function(response){
//			  response格式：{success:true|false,message:"添加成功"|"添加失败"}
			  if(response.success){
				  alert(response.message);
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
		  specificationService.findOne(id).success(function(response){
			  $scope.entity=response;
		  })
	  }
	  
	 

	  $scope.dele=function(){
		  if($scope.selectIds.length==0){
			  return ;
		  }
		  
		  var flag = window.confirm("确认要删除您选择的数据吗？");
		  
		  if(flag){
			  specificationService.dele($scope.selectIds).success(function(response){
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
		  specificationService.search(pageNum,pageSize,$scope.searchEntity).success(function(response){
			 $scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		  }) 
		  
	  }
})