package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {
    //rides中的购物车
    private static final  String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        /**
         * 1、验证商品是否存在，商品的启用状态是否启用
         * 2、如果该商品对应的商家不存在在购物车列表中；则重新加商家及其对应的商品
         * 3、如果该商品对应的商家存在在购物车列表中；那么判断商品是否存在若是则购买数量叠
         加，否则新加入商品到该商家
         */
        //1、验证商品是否存在，商品的启用状态是否启用
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            throw  new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())){
            throw  new RuntimeException("商品状态不合法");
        }
        String sellerId = item.getSellerId();
        Cart cart = findCartBySellerId(cartList,sellerId);
        if (cart ==null){
            if (num>0){
                //2、如果该商品对应的商家不存在在购物车列表中；则重新加商家及其对应的商品
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<>();
                TbOrderItem orderItem = createOrderItem(item,num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);

                cartList.add(cart);
            }else{
                throw  new RuntimeException("购买数量不合法");
            }
        }else{
          //3、如果该商品对应的商家存在在购物车列表中；那么判断商品是否存在若是则购买数量叠加，否则新加入商品到该商家
            TbOrderItem orderItem = findOrodeItemBytemId(cart.getOrderItemList(),itemId);
            if (orderItem!=null){
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //说明购买数量小于0,删除商品
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果商品删除后购物车的明显没有任何商品,就要删除购物车
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }else{
                if (num>0){
                    orderItem= createOrderItem(item,num);
                    cart.getOrderItemList().add(orderItem);
                }else{
                    throw new RuntimeException("购买数量不合法");
                }
            }
        }


        return cartList;
    }

    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList!=null){
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListByUsername(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,newCartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //任何一个集合并够可以
        for (Cart cart :cartList1){
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }

    private TbOrderItem findOrodeItemBytemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList!=null&& orderItemList.size()>0 ){
            for (TbOrderItem orderItem : orderItemList) {
                if (itemId.equals(orderItem.getItemId())){
                    return orderItem;
                }
            }
        }
     return null;
    }


    //构造购物车商品明细
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size()>0 ){
            for (Cart cart : cartList) {
                if (sellerId.equals(cart.getSellerId())){
                    return  cart;
                }
            }
        }
        return  null;
    }

}
