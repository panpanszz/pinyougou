package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {
    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 根据订单编号查询订单的支付状态
     * @param outTradeNo 订单号
     * @return 操作结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("查询支付状态失败");

        try {
            //3分钟查找
            int count = 0;
            while(true){
                //编写处理器方法无限去查询支付系统中订单的支付状态
                Map<String,String > map = weixinPayService.queryPayStatus(outTradeNo);
                if (map==null){
                    //查询失败就退出循环
                    break;
                }
                if ("SUCCESS".equals(map.get("trade_state"))){
                    //如果查询订单已经支付,调用业务方法更新订单状态,返回查询成功
                    orderService.updateOrderStatus(outTradeNo,map.get("transaction_id"));
                    result=Result.ok("查询支付状态成功");
                    break;
                }
                count++;
                if (count>=60){
                    result = Result.fail("支付超时");
                    break;
                }


                //每隔3秒
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 根据支付日志id 到微信支付创建支付订单并返回支付二维码地址等信息
     *outTradeNo 支付日志id
     * 返回支付二维码地址信息
     */
    @GetMapping("/createNative")
    public Map<String,String> createNative(String outTradeNo){
        //查找支付日志信息
        TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
        if (payLog!=null){
            return weixinPayService.createNative(outTradeNo,payLog.getTotalFee().toString());
        }
        return new HashMap<>();
    }

}
