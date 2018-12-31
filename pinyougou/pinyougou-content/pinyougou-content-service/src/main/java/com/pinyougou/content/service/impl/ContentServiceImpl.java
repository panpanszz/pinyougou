package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //内容列表在redis中的键名key的名称
    private static final String CONTENT_LIST = "CONTENT_LIST";

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //更新分类对应的redis缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 将分类id在redis中对应的内容列表删除
     * @param categoryId 分类id
     */
    private void updateContentListInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(CONTENT_LIST).delete(categoryId);
    }

    @Override
    public void update(TbContent tbContent) {
        //查询老内容
        TbContent oldContent = findOne(tbContent.getId());

        super.update(tbContent);

        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())) {
          //说明内容分类被修改过，需要将原来分类的内容列表删除
          updateContentListInRedisByCategoryId(oldContent.getCategoryId());
        }

        //更新分类对应的内容列表
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        //根据内容id数组查询数据库中对应的内容列表
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> contentList = contentMapper.selectByExample(example);
        //遍历每个内容对应的内容分类删除其redis中的数据
        if (contentList != null && contentList.size() > 0) {
            for (TbContent tbContent : contentList) {
                updateContentListInRedisByCategoryId(tbContent.getCategoryId());
            }
        }
        //删除内容
        super.deleteByIds(ids);
    }

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;
        try {
            //从redis中查询内容分类对应的内容列表找到则返回
            contentList = (List<TbContent>) redisTemplate.boundHashOps(CONTENT_LIST).get(categoryId);
            if (contentList != null && contentList.size() > 0) {
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1、查询有效的分类对应的内容列表根据排序字段降序排序
        //select * from tb_content where category_id = ? and status='1' order by sort_order desc
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "1");
        criteria.andEqualTo("categoryId", categoryId);

        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //将内容列表存入redis
            redisTemplate.boundHashOps(CONTENT_LIST).put(categoryId, contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2、返回列表
        return contentList;
    }
}
