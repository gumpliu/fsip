package com.yss.fsip.web;

import com.yss.fsip.constants.FSIPErrorCode;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.generic.Result;
import com.yss.fsip.generic.ResultFactory;
import com.yss.fsip.generic.service.BaseService;
import com.yss.fsip.util.AssertMsgUtil;
import com.yss.fsip.util.CollectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

/**
 * 
 * 基础controller，提供基础增删改查功能
 * 
 * @author LSP
 */
public abstract class BaseController<T,ID> {

    public abstract BaseService<T,ID> getBaseService();

	/**
	 * 持久化
	 * 
	 * @param model
	 */
    public T save(T model) {
       return getBaseService().save(model);
    }

    /**
     * 持久化
     *
     * @param models
     */
    public List<T> save(Iterable<T> models) {
        return getBaseService().save(models);
    }


    /**
     * 通过主鍵刪除
     * 
     * @param id
     */
    public void deleteById(ID id) {
        getBaseService().deleteById(id);
    }
    
    /**
     * 通过对象删除
     * 
     * @param T entity
     */
    public void delete(T entity) {
        getBaseService().delete(entity);
    }
    
    /**
     * 删除所有
     */
    public void deleteAll() {
        getBaseService().deleteAll();
    }

    /**
     * 通过ID查找
     * 
     * @param id
     * @return
     */
    public T findById(ID id) {
        return getBaseService().findById(id);
    }
    
    /**
     * 单表分页，每页显示20条数据
     * 
     * @param pageNumber 当前页
     * @return
     */
    public Page<T> findPage(int pageNumber){ 	
    	return getBaseService().findPage(pageNumber);
    }
    
    /**
     * 单表分页
     * 
     * @param pageNumber 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    public Page<T> findPage(int pageNumber, int pageSize){
   	   return getBaseService().findPage(pageNumber, pageSize);
   }
    
    /**
     * 查询所有
     * 
     * @return
     */
    public List<T> findAll() {
        return getBaseService().findAll();
    }

    /**
     * 查询所有排序
     *
     * @param sort
     * @return
     */
    public List<T> findAll(Sort sort){

        return getBaseService().findAll(sort);

    }

    /**
     * 查询指定ids
     * @param ids
     * @return
     */
    public List<T> findAllById(Iterable<ID> ids){

        return getBaseService().findAllById(ids);
    }

    @ResponseBody
    @RequestMapping(value="/check", method = RequestMethod.POST, produces="application/json")
    public Result check(@RequestParam String ids) {
        Assert.hasLength(ids, AssertMsgUtil.getMsg(FSIPErrorCode.PARAM_ERR, "ids"));

        Set<String> idSet = CollectionUtil.convertStr2Set(ids);
        String userId = FSIPContextFactory.getContext().getUserId();
        this.getBaseService().check(idSet,userId);
        return ResultFactory.success();
    }

    @ResponseBody
    @RequestMapping(value="/unCheck", method = RequestMethod.POST, produces="application/json")
    public Result unCheck(@RequestParam String ids) {
        Assert.hasLength(ids, AssertMsgUtil.getMsg(FSIPErrorCode.PARAM_ERR, "ids"));

        Set<String> idSet = CollectionUtil.convertStr2Set(ids);
        String userId = FSIPContextFactory.getContext().getUserId();
        this.getBaseService().unCheck(idSet,userId);
        return ResultFactory.success();
    }
}
