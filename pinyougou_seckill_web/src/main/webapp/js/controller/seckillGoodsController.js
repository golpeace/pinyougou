app.controller("seckillGoodsController",function($scope,$location,$interval,seckillGoodsService){
	 
	
	$scope.saveOrder=function(){
		seckillGoodsService.saveOrder($scope.entity.id).success(function(response){
			if(response.success){
				location.href="pay.html";
			}else{
				alert(response.message);
			}
			
		})
		
		
	}
	
	
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(function(response){
			
			$scope.list = response;
			
		})
	} 
	
	$scope.findOne=function(){
		var id = $location.search()['id'];
		seckillGoodsService.findOne(id).success(function(response){
			$scope.entity = response;
			
			var allSeconds =Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);
			 
			var times = $interval(function(){
				$scope.timeString = $scope.timeConverterString(allSeconds);
				allSeconds--;
				if(allSeconds==0){
					$interval.cancel(times);
				}
			},1000)
		})
		
		
//		$scope.timeStr=5;
//		getTime()获取毫秒
//		Math.floor 向下取整   12.2323--->12    12.787---->12

//		天
//		小时
//		分
//		秒
//		13天 12:03:09
		
	}
	
	$scope.timeConverterString=function(allSeconds){
		var days = Math.floor(allSeconds/60/60/24);
//		13.72123
		var hours =  Math.floor((allSeconds - days*24*60*60)/60/60);
		var minuts =  Math.floor((allSeconds - days*24*60*60 - hours*60*60)/60);
		var seconds =  Math.floor(allSeconds - days*24*60*60 - hours*60*60 - minuts*60);
		var timeString ="";
		if(days!=0){
			timeString+=days+"天  ";
		}
		if(hours<10){
			hours="0"+hours;
		}
		if(minuts<10){
			minuts="0"+minuts;
		}
		if(seconds<10){
			seconds="0"+seconds;
		}
		
		return  timeString +hours+":"+minuts+":"+seconds;
		
	}
	
})