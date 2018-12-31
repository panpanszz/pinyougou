package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体类
     */
    T findOne(Serializable id);

    /**
     * 查询全部
     * @return 列表
     */
    List<T> findAll();

    /**
     * 根据条件查询
     * @param t 查询条件
     * @return 列表
     */
    List<T> findByWhere(T t);

    /**
     * 根据分页信息查询
     * @param page 页号
     * @param rows 页大小
     * @return 分页结果对象
     */
    PageResult findPage(Integer page, Integer rows);

    /**
     * 根据分页信息条件查询
     * @param page 页号
     * @param rows 页大小
     * @param t 查询条件
     * @return 分页结果对象
     */
    PageResult findPage(Integer page, Integer rows, T t);

    /**
     * 新增
     * @param t 新增对象
     */
    void add(T t);

    /**
     * 根据主键更新
     * @param t 更新的对象
     */
    void update(T t);

    /**
     * 批量删除
     * @param ids id数组
     */
    void deleteByIds(Serializable[] ids);

}
