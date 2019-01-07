app.controller("payController",function ($scope,$location,cartService,payService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;

        });
    };
    //生成二维码
    $scope.createNative = function () {
        //1,接收浏览器地址栏的订单号
        $scope.outTradeNo = $location.search()["outTradeNo"];
        debugger;
        
        //2,发送请求到后台
        payService.createNative($scope.outTradeNo).success(function (response) {
            if ("SUCCESS"==response.result_code){
                //总金额
                $scope.money=(response.totalFee/100).toFixed(2);

                //3,生成二维码
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"Q",
                    value:response.code_url
                });
                //查询支付状态
                queryPayStatus($scope.outTradeNo);

            }else{
                alert("生成二维码失败");
            };
            
        });
    };
    //查询支付状态
    queryPayStatus=function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
           if (response.success){
               location.href = "paysuccess.html#?money=" + $scope.money;
           }else{
               if ("支付超时"==response.message){
                   alert("支付超时");
                   //重新生成二维码
                   $scope.createNative();
               }else {
                   //支付失败页面
                   location.href="payfail.html";
               }

           };
        });
    };

    //获取总金额
    $scope.getMoney = function () {
        $scope.money = $location.search()["money"];
    }
    
})