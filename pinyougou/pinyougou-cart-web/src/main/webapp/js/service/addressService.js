app.service("addressService",function ($http) {
   this.findAddressList = function () {
       return $http.get("../address/findAddressList.do?t="+Math.random());
   }
   this.submitOrder =  function (order) {
       return $http.post("../order/add.do",order);
   }
});