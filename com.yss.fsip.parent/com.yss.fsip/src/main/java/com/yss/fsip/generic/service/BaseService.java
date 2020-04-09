package com.yss.fsip.generic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

/**
 * service 基础功能
 *
 * @Author: gumpLiu
 * @Date: 2019-12-03 13:30
 */
public interface BaseService<T, ID> {
    /**
     * 持久化
     *
     * @param model
     */
    public T save(T model);

    /**
     * 持久化
     *
     * @param models
     */
    public List<T> save(Iterable<T> models);

    /**
     * 通过主鍵刪除
     *
     * @param id
     */
    public void deleteById(ID id);

    /**
     * 通过对象删除
     *
     * @param T entity
     */
    public void delete(T entity);

    /**
     * 删除所有
     */
    public void deleteAll();


    /**
     * 通过ID查找
     *
     * @param id
     * @return
     */
    public T findById(ID id);

    /**
     * 单表分页，每页显示20条数据
     *
     * @param pageNumber 当前页
     * @return
     */
    public Page<T> findPage(int pageNumber);

    /**
     * 单表分页
     *
     * @param pageNumber 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    public Page<T> findPage(int pageNumber, int pageSize);

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> findAll();


    /**
     * 查询所有排序
     *
     * @param sort
     * @return
     */
    List<T> findAll(Sort sort);

    /**
     * 查询指定ids
     * @param ids
     * @return
     */
    List<T> findAllById(Iterable<ID> ids);

    /**
     * 批量审核
     * @param ids
     * @param userId
     * @return
     */
    void check(Set<String> ids, String userId);

    /**
     * 批量反审核
     * @param ids
     * @param userId
     * @return
     */
    void unCheck(Set<String> ids, String userId);
}
