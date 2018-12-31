package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    /**
     * 在门户系统查询有效的轮播广告类型并根据排序值降序排序的广告内容数据显示在首页轮播广告区域部分。
     * @param categoryId 广告分类id
     * @return 内容列表
     */
    List<TbContent> findContentListByCategoryId(Long categoryId);
}