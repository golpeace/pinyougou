 //控制层 
app.controller('contentCategoryController' ,function($scope,contentCategoryService){	
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentCategoryService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		contentCategoryService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		contentCategoryService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=contentCategoryService.update( $scope.entity ); //修改  
		}else{
			serviceObject=contentCategoryService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findAll();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		contentCategoryService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findAll();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		contentCategoryService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.selectIds=[];

	  $scope.updateSelection=function($event,id){
		  if($event.target.checked){ //勾选
//				 向数组中添加数据
			  $scope.selectIds.push(id); 
		  }else{ //取消勾选
//				  从数组中移除数据  splice   [1,2,3]
//				  $scope.selectIds.splice(即将移除数据的脚标索引,数量);
			  var index = $scope.selectIds.indexOf(id);
			  $scope.selectIds.splice(index,1);
		  }
//			  判断复选框是勾选还是取消勾选
	  }
    
});	
