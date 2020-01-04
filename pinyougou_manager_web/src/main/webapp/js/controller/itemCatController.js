 //控制层 
app.controller('itemCatController' ,function($scope,itemCatService,typeTemplateService){	
	
	
//	查询所有的模板数据
	$scope.findAllTypeTemplate=function(){
		typeTemplateService.findAll().success(function(response){
			$scope.typeTemplateList = response;
		})
	}
	
	$scope.entity1=null;  //用来存放一级分类对象
	$scope.entity2=null;  //用来存放二级分类对象
	
	$scope.parentId=0;  //用来保留即将保存数据的parentId;
	
	$scope.grade=1; //代表显示的是一级分类数据
	
//	这是一个用来修改显示数据级别的方法  ，还需要为面包屑上的变量赋值
	$scope.setGrade=function(grade,pojo){
		$scope.grade=grade;
		
		if($scope.grade==2){ //当前显示的是第二级分类数据   
			$scope.entity1=pojo;
			$scope.entity2=null; //面包屑上的最后一个位置清空
			
			$scope.parentId=pojo.id;
			
		}
		
		if($scope.grade==3){
			$scope.entity2=pojo;
			$scope.parentId=pojo.id;
		}
		
		
	}
	
	$scope.findByParentId=function(parentId){
		
		if(parentId==0){
			$scope.entity1=null;  //用来存放一级分类对象
			$scope.entity2=null;  //用来存放二级分类对象
			$scope.grade=1; //代表显示的是一级分类数据
			
			$scope.parentId=0;
		}
		
		
		itemCatService.findByParentId(parentId).success(function(response){
			$scope.list = response;
		})
		
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  	
		
		$scope.entity['parentId']=$scope.parentId;
		
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
