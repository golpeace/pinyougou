 //控制层 
app.controller('goodsController' ,function($scope,$controller ,itemCatService  ,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.updateAuditStatus=function(auditStatus,info){
		if($scope.selectIds.length==0){
			return;
		}
		
		if(window.confirm("确认要"+info+"您选择的商品吗")){
			goodsService.updateAuditStatus(auditStatus,$scope.selectIds).success(function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}else{
					alert(response.message);
				}
				
			})
		}
		
		
	}
	
	
	$scope.status=["未审核","已审核","已驳回"];
	
	$scope.itemCat={};
	
	$scope.findAllItemCat=function(){
		itemCatService.findAll().success(function(response){
//			response = [{"id":1,"name":"图书、音像、电子书刊"},{"id":2,"name":"电子书刊"}]-
//			------->itemCat = {"1":"图书、音像、电子书刊","2":"电子书刊",...,"1207":"二级分类数据测试222","1208":"三级分类数据测试"}
			
			for (var i = 0; i < response.length; i++) {
				$scope.itemCat[response[i].id]=response[i].name;
			}
			
		})
		
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
