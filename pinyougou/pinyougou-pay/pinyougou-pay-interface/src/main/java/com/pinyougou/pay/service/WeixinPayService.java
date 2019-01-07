package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    /**
     * 根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @param outTradeNo 支付日志 id
     * @param totalFee 支付总金额
     * @return 支付二维码地址等信息
     */
    Map<String,String> createNative(String outTradeNo, String totalFee);

    Map<String,String> queryPayStatus(String outTradeNo);
}
