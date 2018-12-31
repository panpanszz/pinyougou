package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 保存商品数据（基本、描述、sku列表）
     * @param goods 基本、描述、sku列表
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //获取当前登录用户名
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //商品的商家
            goods.getGoods().setSellerId(sellerId);
            //商品的状态为 0 未审核
            goods.getGoods().setAuditStatus("0");

            goodsService.addGoods(goods);
            return Result.ok("增加商品成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加商品失败");
    }

    /**
     * 根据商品spu id查询基本、描述、sku列表
     * @param id 商品spu id
     * @return 商品信息（基本、描述、sku列表）
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsByGoodsId(id);
    }

    /**
     * 更新基本、描述、sku列表
     * @param goods 商品信息（基本、描述、sku列表）
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取原有这个商品的商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());

            if (sellerId.equals(goods.getGoods().getSellerId()) && sellerId.equals(oldGoods.getSellerId())) {
                goodsService.updateGoods(goods);
                return Result.ok("修改成功");
            } else {
                return Result.ok("非法修改");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //设置商家作为查询条件
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }

    /**
     * 批量更新商品的审核状态
     * @param ids 商品spu id数组
     * @param status 要修改的状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids, status);
            return Result.ok("修改商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改商品状态失败");
    }

}
