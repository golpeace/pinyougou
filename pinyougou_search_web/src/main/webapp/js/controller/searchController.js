app.controller("searchController",function($scope,$location,searchService){
	
//	$scope.paramMap={"keyword":'三星',"brand":'三星',"category":'手机',"spec":{屏幕尺寸:4.5寸,机身内存:16G},price:''};
	$scope.paramMap={"keyword":'',"brand":'',"category":'',"spec":{},"price":'',"order":'asc',"page":1};
	
	$scope.initSearch=function(){
//		 获取url上的keyword
		$scope.paramMap.keyword=$location.search()["keyword"];
		if($scope.paramMap.keyword==null||$scope.paramMap.keyword==undefined){
			$scope.paramMap.keyword="华为";
		}
		$scope.search();
	}
	
	$scope.addParamToMap=function(key,value){
		$scope.paramMap[key]=value;
		$scope.search();
	}
	$scope.removeParamFromMap=function(key){
		$scope.paramMap[key]='';
		$scope.search();
	}
	
	$scope.addSpecParamToMap=function(key,value){
		$scope.paramMap.spec[key]=value;
		$scope.search();
	}
	$scope.removeSpecParamFromMap=function(key){
//		$scope.paramMap.spec={屏幕尺寸:4.5寸,机身内存:16G}  key:屏幕尺寸 ----->{机身内存:16G}
		delete $scope.paramMap.spec[key];
		$scope.search();
	}
	
	
	
	$scope.searchByKeyWord=function(){
		$scope.paramMap.brand='';
		$scope.paramMap.category='';
		$scope.paramMap.spec={};
		$scope.paramMap.price='';
		$scope.paramMap.order='asc';
		$scope.paramMap.page='1';
		$scope.search();
	}
	
	$scope.search=function(){
		searchService.search($scope.paramMap).success(function(response){
			$scope.resultMap = response;
//			$scope.pageList=[];
//			for (var i = 1; i <= response.totalPage; i++) {
//				$scope.pageList.push(i);
//			}
//			$scope.pageList=[1,2,3,4]
			
			buildPageLabel();
		})
	}
	
	function buildPageLabel() {
        $scope.pageList = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于 5 页,显示部分页码
            if ($scope.paramMap.page <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点
            } else if ($scope.paramMap.page >= lastPage - 2) {//如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4;  //后 5 页
                $scope.lastDot = false;//后边没点
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.paramMap.page - 2;
                lastPage = $scope.paramMap.page + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageList.push(i);
        }
    }

	
	
})