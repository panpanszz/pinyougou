app.service("uploadService",function ($http) {

    this.uploadFile = function () {
        //创建html5的表单数据对象
        var formData = new FormData();
        //设置表单项
        formData.append("file", file.files[0]);
        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            headers:{"Content-Type": undefined},
            transformRequest: angular.identity
        });
    };
});