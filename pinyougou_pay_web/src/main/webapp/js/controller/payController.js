app.controller("payController",function($scope,$location,payService){
	 
	$scope.createNative=function(){
		payService.createNative().success(function(response){
			  $scope.resultMap = response;
			  
//			  产生二维码
			   new QRious({
			      element: document.getElementById('qrious'),
			      size: 250,
			      value: response.code_url
			    });
			   
//			   马上查询是否支付
			   $scope.queryPayStatus(response.out_trade_no);
		})
		
	}
	
	$scope.queryPayStatus=function(out_trade_no){
		payService.queryPayStatus(out_trade_no).success(function(response){
			if(response.success){
				location.href="paysuccess.html#?totalFee="+$scope.resultMap.total_fee;
			}else{
				if(response.message=="支付超时"){
//					美工应该提供一个 刷新二维码的功能
					$scope.payOutInfo="二维码已超时，请刷新页面";
					  new QRious({
					      element: document.getElementById('qrious'),
					      size: 250,
					      value: ""
					    });
//					$scope.createNative();//重新生成二维码
				}else{
					location.href="payfail.html";
				}
				
//				
			}
		})
		
	}
	
	$scope.showMoney=function(){
		return $location.search()['totalFee'];
	}
	
})