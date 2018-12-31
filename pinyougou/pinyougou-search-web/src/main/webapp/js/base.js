var app = angular.module("pinyougou",[]);

//定义过滤器
var filter = app.filter("trustHtml", ["$sce", function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);