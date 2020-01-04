 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller ,brandService ,specificationService ,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
//	把[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]转成“,网络,机身内存”
//	[{"id":26,"text":"海澜之家"},{"id":27,"text":"拉夏贝尔"},{"id":4,"text":"小米"}]
//	[{"text":"内存大小"},{"text":"颜色"}]  转成“内存大小,颜色”
	$scope.toStringFromArray=function(array){
		array = JSON.parse(array);
		var str="";
		for (var i = 0; i < array.length; i++) {
			if(i==0){
				str += array[i].text;
			}else{
				str += ","+array[i].text;
			}
		}
		return str;
	}
	
	
	$scope.entity={customAttributeItems:[]};
//	动态添加扩展属性
	$scope.addCustomAttributeItems=function(){
		$scope.entity.customAttributeItems.push({});
	}
//	动态删除扩展属性
	$scope.deleCustomAttributeItems=function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}
//	$scope.brandList={data:[{id:1,text:"联想"},{id:2,text:"小米"}]};
	
	$scope.findBrandList=function(){
		brandService.findBrandList().success(function(response){
//			response = [{id:1,text:"联想"},{id:2,text:"小米"}]
			$scope.brandList = {data:response};//[{"firstChar":"L","id":1,"name":"联想"},{"firstChar":"H","id":2,"name":"华为"}]
		})
	}
	
	$scope.findSpecList=function(){
		specificationService.findSpecList().success(function(response){
//			response = [{id:1,text:"联想"},{id:2,text:"小米"}]
			$scope.specList = {data:response};//[{"firstChar":"L","id":1,"name":"联想"},{"firstChar":"H","id":2,"name":"华为"}]
		})
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				response.specIds = JSON.parse(response.specIds)
				response.brandIds = JSON.parse(response.brandIds)
				response.customAttributeItems = JSON.parse(response.customAttributeItems)
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  
		
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
