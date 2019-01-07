package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    String addOrder(TbOrder order);

    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    void updateOrderStatus(String outTradeNo, String transaction_id);
}