app.controller("cartController",function($scope,cartService){
	
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(function(response){
			if(response.success){
				$scope.findCartList();
			}else{
				alert(response.message);
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