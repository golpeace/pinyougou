app.service("searchService",function($http){
	 this.search=function(paramMap){
		return $http.post("./solr/searchFromSolr",paramMap);
	 }
})