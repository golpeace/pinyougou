app.controller("indexController",function($scope,indexService){
	
	$scope.findBannerList=function(){
		indexService.findContentByCategoryId(1).success(function(response){
			$scope.bannerList = response;
		})
	}
	
	$scope.search=function(){
		if($scope.keyword==null||$scope.keyword==""){
			$scope.keyword="三星";
		}
		location.href="http://search.pinyougou.com/search.html#?keyword="+$scope.keyword;
	}
	
})