app.service("uploadService",function($http){
	
	this.uploadFile=function(){
//		使用angularJS的文件上传
		var formData = new FormData();//html5 存放表单数据
		
		formData.append("file",file.files[0]);
		
		return $http({
			method:"post",
			url:'../upload',
			data:formData,
			headers: {'Content-Type':undefined},
			transformRequest: angular.identity
		})
	}

	
	
})