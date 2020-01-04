app.controller("registerController",function($scope,registerService){
	$scope.entity={phone:''};
	//发送短信
	$scope.sendCode=function(){
		
		if($scope.entity.phone==''||$scope.entity.phone==null){
			alert("手机号没填！");
			return;
		}
		var reg =/^1[3|4|5|6|7|8|9|][0-9]{9}$/;
		if(!reg.test($scope.entity.phone)){
			alert("格式不对！");
			return;
		}
		registerService.sendCode($scope.entity.phone).success(function(response){
//			if(response.success){
				alert(response.message);
//			}
		})
	}
	
	$scope.register=function(){
		if($scope.entity.password!=$scope.password2){
			alert("两次密码输入不一致！");
			return;
		}
		registerService.register($scope.entity,$scope.code).success(function(response){
			if(response.success){
				location.href="home-index.html";
			}else{
				alert(response.message);
			}
		})
		
	}
	 
})