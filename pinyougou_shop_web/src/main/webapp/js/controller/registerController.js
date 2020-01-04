app.controller("registerController",function($scope,sellerService){
	
	// 保存
	$scope.save = function() {
		//商家注册
		sellerService.add($scope.entity).success(function(response) {
			if (response.success) {
				// 跳转到商品登录页面
				alert("请耐心等待审核结果，24小时内出结果");
				location.href = "shoplogin.html";
			} else {
				alert(response.message);
			}
		});
	}
	
})