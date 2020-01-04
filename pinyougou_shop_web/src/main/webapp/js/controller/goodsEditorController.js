app.controller("goodsEditorController",function($scope,uploadService,itemCatService,typeTemplateService,goodsService){
	
//	[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"},
//		{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWs2ABppQAAETwD7A1Is142.jpg"}]
	
	$scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[]},tbGoods:{isEnableSpec:'1'},itemList:[]};
	
	
//	更新即将保存的规格数据 [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
	$scope.updateSpecificationItems=function($event,key,value){
//		如果是勾选
		if($event.target.checked){
			var specificationItem = matchObject($scope.entity.tbGoodsDesc.specificationItems,key);
			if(specificationItem==null){ //代表追加的数据没有相关的对象
				$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":key,"attributeValue":[value]});
			}else{  //代表追加的数据找到相关的对象
				specificationItem.attributeValue.push(value);
			}
		}else{//如果是取消
			var specificationItem = matchObject($scope.entity.tbGoodsDesc.specificationItems,key);
			var index = specificationItem.attributeValue.indexOf(value);
			specificationItem.attributeValue.splice(index,1);
			if(specificationItem.attributeValue.length==0){
				var _index = $scope.entity.tbGoodsDesc.specificationItems.indexOf(specificationItem);
				$scope.entity.tbGoodsDesc.specificationItems.splice(_index,1)
			}
			
		}
		
//		[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
//			{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
		
		createItemList();
		
	}
	
//	创建sku数据列表
	function createItemList(){
//		[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
//		{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
		var specItems = $scope.entity.tbGoodsDesc.specificationItems;

		$scope.entity.itemList=[{spec:{},price:1000,num:1000,status:"1",ifDefault:"0"}];
		
		for (var i = 0; i < specItems.length; i++) {
			$scope.entity.itemList=addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);
		}
		
	}
//	attributeValue:["移动3G","移动4G"]
	function addColumn(itemList,attributeName,attributeValue){
		var newItemList=[];
		for (var i = 0; i < itemList.length; i++) {
			var oldItem = itemList[i];
			for (var j = 0; j < attributeValue.length; j++) {
				var newItem = JSON.parse(JSON.stringify(itemList[i]));//深克隆
				newItem.spec[attributeName] = attributeValue[j];
				newItemList.push(newItem);
			}
		}
		return newItemList;
//		itemList=[{spec:{机身内存:16G},price:1000,num:1000,status:1,ifDefault:0},
//			{spec:{机身内存:32G},price:1000,num:1000,status:1,ifDefault:0}]
	}
	
	
//	specificationItems = [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
//	key =  网络制式
//	判断specificationItems中是否有此对象
    function matchObject(specificationItems,key){
    	for (var i = 0; i < specificationItems.length; i++) {
    		if(specificationItems[i].attributeName==key){
    			return specificationItems[i];
    		}
		}
    	return null;
    }
	
	
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response){
			if(response.success){
//				{success:true|false,message:url|失败}
				$scope.image.url=response.message;
			}else{
				alert(response.message);
			}
			
		})
		
	}
	
//	动态添加图片对象
	$scope.addImages=function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image);
	}
//	动态删除图片对象
	$scope.deleteImages=function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
	}
	
	
	
	
//	查询一级分类
	$scope.findCategory1List=function(){
		itemCatService.findByParentId('0').success(function(response){
			$scope.category1List=response;
		})	
	}
	
	//angularJS提供的 观察事件 相当于 onChange事件   "entity.tbGoods.category1Id"代表的是需要观察的数据
	$scope.$watch("entity.tbGoods.category1Id",function(newValue,oldValue){ //如果有一个参数时 newValue
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.category2List=response;
//			清空三级列表
			$scope.category3List=[];
			$scope.entity.tbGoods.typeTemplateId="";
		})	
	})
	
	//angularJS提供的 观察事件 相当于 onChange事件   "entity.tbGoods.category2Id"代表的是需要观察的数据
	$scope.$watch("entity.tbGoods.category2Id",function(newValue,oldValue){ //如果有一个参数时 newValue
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.category3List=response;
		})	
	})
	
	//angularJS提供的 观察事件 相当于 onChange事件   "entity.tbGoods.category2Id"代表的是需要观察的数据
	$scope.$watch("entity.tbGoods.category3Id",function(newValue,oldValue){ //如果有一个参数时 newValue
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.tbGoods.typeTemplateId=response.typeId;
		})	
	})
	
	//angularJS提供的 观察事件 相当于 onChange事件   "entity.tbGoods.typeTemplateId"代表的是需要观察的数据
	$scope.$watch("entity.tbGoods.typeTemplateId",function(newValue,oldValue){ //如果有一个参数时 newValue
//		根据模板ID获取品牌数据  {id:35,name:手机，brandIds：[{"id":1,"text":"联想"}]}
		typeTemplateService.findOne(newValue).success(function(response){  // response模板对象
			$scope.brandList=JSON.parse(response.brandIds); //品牌数据
			$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems); //扩展属性数据
//			[{"text":"内存大小"},{"text":"颜色"}]
			
			
		});
		
		typeTemplateService.findSpecList(newValue).success(function(response){
//			specList : [{"id":27,"text":"网络",options:[]},{"id":32,"text":"机身内存"}]
			$scope.specList=response;
		})
		
		
	})
	
	
	
	$scope.save=function(){
		
		$scope.entity.tbGoodsDesc["introduction"]=editor.html(); //从富文本编辑器中取值   赋值到 entity.tbGoodsDesc的introduction属性
		goodsService.add($scope.entity).success(function(response){
			
			if(response.success){
				location.href="goods.html";
			}else{
				alert(response.message);
			}
			
		})
	}
	
	
})