package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/content")
@RestController
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 在门户系统查询有效的轮播广告类型并根据排序值降序排序的广告内容数据显示在首页轮播广告区域部分。
     * @param categoryId 广告分类id
     * @return 内容列表
     */
    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId){
        return contentService.findContentListByCategoryId(categoryId);
    }
}
