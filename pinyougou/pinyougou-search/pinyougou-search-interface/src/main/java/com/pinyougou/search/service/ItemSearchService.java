package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 根据搜索关键字和其它条件到solr中查询数据
     * @param searchMap 搜索条件
     * @return 查询结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 批量更新solr中的商品数据
     * @param itemList 商品sku列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 根据spu id数组列表删除solr的数据
     * @param goodsIdsList spu id数组
     */
    void deleteByGoodsIds(List<Long> goodsIdsList);
}
