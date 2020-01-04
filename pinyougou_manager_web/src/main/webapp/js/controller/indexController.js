app.controller("indexController",function($scope,loginService){
	
	$scope.showName=function(){
		loginService.showName().success(function(response){
			$scope.name = JSON.parse( response);
		})
	}
	
})