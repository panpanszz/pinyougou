app.controller("cartController",function ($scope,cartService) {
   $scope.getUsername = function () {
       cartService.getUsername().success(function (response) {
           $scope.username = response.username;
       });
   };


    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //计算购买总数和价格
            $scope.totalValue = cartService.sumTotalValue(response);

        });
    };


    $scope.addItemToCartList = function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
           if (response.success){
               $scope.findCartList();
           }else{
               alert(response.success)
           }
        });
    };

});