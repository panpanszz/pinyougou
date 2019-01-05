app.controller("orderInfoController",function ($scope,addressService,cartService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };
    //获取当前登录人的收货地址列表
    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for (var i=0;i<response.length;i++) {
                     var address = response[i];
                     if ("1"==address.isDefault){
                         $scope.address = address;
                         break;
                     }
            }


        });
    };

    //计算的购买的总价
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
           $scope.cartList = response;

           //计算总价
            $scope.totalValue = cartService.sumTotalValue(response);
        });
    };
    //判断地址是否选中
    $scope.isSelectedAddress=function (address) {
        if ($scope.address==address){
            return true;
        }
        return false;
    };

     //选中地址
    $scope.selectAddress =  function (address) {
        $scope.address=address;
    };
    //支付方式,默认
    $scope.order = {"paymentType":"1"};
    //支付方式
    $scope.selectPaymentType= function (type) {
        $scope.order.paymentType = type;
        
    }

    //提交订单
    $scope.submitOrder= function () {
        $scope.order.receiverAreaName =  $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        addressService.submitOrder($scope.order).success(function (response) {
           if (response.success){
               if ($scope.order.paymentType=="1"){
                   //携带支付业务id,跳转到支付页面
                   location.href = "pay.html?outTradeNo="+response.message;
               } else{
                   location.href = "paysuccess.html";
               }
           }else{
               alert(response.message);
           }
        });
    };

});