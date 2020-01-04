app.controller("orderController",function($scope,addressService,cartService,orderService){
	
//	 `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
//	  `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
//	  `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
//	  `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
//	  `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
	
	$scope.entity={paymentType:'1',sourceType:'2'};
	
	$scope.saveOrder=function(){
		
		$scope.entity["receiverAreaName"]=$scope.defaultAddress.address;
		$scope.entity["receiverMobile"]=$scope.defaultAddress.mobile;
		$scope.entity["receiver"]=$scope.defaultAddress.contact;
		
		orderService.add($scope.entity).success(function(response){
			if(response.success){
				location.href="http://pay.pinyougou.com/pay.html";
			}else{
				alert(response.message);
			}
		})
		
	}
	
	
	//选择收件人
	$scope.selecedAddress=function(pojo){
		$scope.defaultAddress=pojo;
	}
	
	//判断当前地址是否是选择的地址
	$scope.isSelectedAddress=function(pojo){
		return pojo==$scope.defaultAddress;
	}
	
	
	$scope.defaultAddress=null;//默认地址  从addressList查询
	
	$scope.findAddressList=function(){
		addressService.findAddressByUser().success(function(response){
			$scope.addressList = response;
			
			for (var i = 0; i < response.length; i++) {
				if(response[i].isDefault=='1'){
					$scope.defaultAddress=response[i];
					break;
				}
			}
			if($scope.defaultAddress==null){
				$scope.defaultAddress=response[0];
			}
			
		})
	}
	 
	
	
	
	
	
	
	 $scope.findCartList=function(){
		 cartService.findCartList().success(function(response){
			 $scope.cartList = response;
			 
			 $scope.totalMomey=0.00;
			 $scope.totalNum=0;
			 
			 for (var i = 0; i < response.length; i++) {
				 var orderItemList = response[i].orderItemList;
				 for (var j = 0; j < orderItemList.length; j++) {
					 $scope.totalMomey+=orderItemList[j].totalFee;
					 $scope.totalNum+=orderItemList[j].num;
				}
				 
			}
		 })
	 }
	 
})