package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //cookie中购物车的名字
    private static final String COOKIE_CART_LIST="PYG_CART_LIST";

    //cookie中购物车列表的最大生存时间为一天
    private  static final  int COOKIE_CART_LIST_MAX_AGE = 3600*24;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;
    /**
     * 获取购物车的数据,没有登录就去cookie中,有就去redis中
     *
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListJsonStr = CookieUtils.getCookieValue(request,COOKIE_CART_LIST,true);
        List<Cart> cookie_cartList;
        if (!StringUtils.isEmpty(cartListJsonStr)){
            cookie_cartList = JSONArray.parseArray(cartListJsonStr,Cart.class);
        }else{
            cookie_cartList =  new ArrayList<>();
        }
        if ("anonymousUser".equals(username)){
            return cookie_cartList;
        }else {
            //已经登录
            List<Cart> redis_cartList =cartService.findCartListByUsername(username);
            //整合购物车
            if (cookie_cartList.size()>0){

                redis_cartList = cartService.mergeCartList(cookie_cartList,redis_cartList);
                //保存最新的购物车到reids
                cartService.saveCartListByUsername(redis_cartList,username);
                //删除cookie购物车
                CookieUtils.deleteCookie(request,response,COOKIE_CART_LIST);

            }
            return redis_cartList;
        }
    }

    /**
     * 获取当前登录用户信息
     * @return
     * 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);

        return map;
    }

    //增加删除
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        try {
            /*//设置允许跨域请求
            response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");
            //允许携带并接收\cookie
            response.setHeader("Access-Control-Allow-Credentials","true");*/

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入到购物车列表
            List<Cart> newCartList = cartService.addItemToCartList(cartList,itemId,num);
            if ("anonymousUser".equals(username)){
                String cartListJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request,response,COOKIE_CART_LIST,cartListJsonStr,COOKIE_CART_LIST_MAX_AGE,true);
            }else{
                //登录
                cartService.saveCartListByUsername(newCartList,username);
            }
            return Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("加入购物车失败");
    }




}
