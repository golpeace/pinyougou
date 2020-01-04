app.controller("baseController",function($scope){
	
	$scope.paginationConf = {
			currentPage: 1,  //当前页码
			totalItems: 10,  //总记录数  从后台获取
			itemsPerPage: 10,//每页显示的条数
			perPageOptions: [10, 20, 30, 40, 50],
			onChange: function(){
				$scope.reloadList();//重新加载
			}
  };
  $scope.reloadList=function(){
//		 分页查询数据 当前页码  每页显示的条数
//        $scope.findByPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
       $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
//     返回：当前页的数据List     总记录数
//brand/findPage/1/10
		
  }

	$scope.selectIds=[];

  $scope.updateSelection=function($event,id){
	  if($event.target.checked){ //勾选
//			 向数组中添加数据
		  $scope.selectIds.push(id); 
	  }else{ //取消勾选
//			  从数组中移除数据  splice   [1,2,3]
//			  $scope.selectIds.splice(即将移除数据的脚标索引,数量);
		  var index = $scope.selectIds.indexOf(id);
		  $scope.selectIds.splice(index,1);
	  }
//		  判断复选框是勾选还是取消勾选
  }
  $scope.searchEntity={};//初始化对象
	
  
  
  
})