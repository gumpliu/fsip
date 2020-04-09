package com.yss.fsip.generic.service;

import com.google.common.collect.Lists;
import com.yss.fsip.annotations.Function;
import com.yss.fsip.constants.CheckableConstants;
import com.yss.fsip.generic.repository.BaseRepository;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @Author: gumpLiu
 * @Date: 2019-12-03 13:21
 */
public abstract class BaseServiceImpl<T,ID> implements BaseService<T,ID> {

    public abstract BaseRepository<T,ID> getBaseRepository();

    /**
     * 持久化
     *
     * @param model
     */
    public T save(T model) {
        return getBaseRepository().save(model);
    }

    /**
     * 持久化
     *
     * @param models
     */
    public List<T> save(Iterable<T> models) {
        return Lists.newArrayList(getBaseRepository().saveAll(models));
    }

    /**
     * 通过主鍵刪除
     *
     * @param id
     */
    public void deleteById(ID id) {
        getBaseRepository().deleteById(id);
    }

    /**
     * 通过对象删除
     */
    public void delete(T entity) {
        getBaseRepository().delete(entity);
    }

    /**
     * 删除所有
     */
    public void deleteAll() {
        getBaseRepository().deleteAll();
    }



    /**
     * 通过ID查找
     *
     * @param id
     * @return
     */
    public T findById(ID id) {
        Optional<T> optional =  getBaseRepository().findById(id);

        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * 单表分页，每页显示20条数据
     *
     * @param pageNumber 当前页
     * @return
     */
    public Page<T> findPage(int pageNumber){
        return getBaseRepository().findPage(pageNumber);
    }

    /**
     * 单表分页
     *
     * @param pageNumber 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    public Page<T> findPage(int pageNumber, int pageSize){
        return getBaseRepository().findPage(pageNumber, pageSize);
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> findAll() {
        return getBaseRepository().findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return getBaseRepository().findAll(sort);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return getBaseRepository().findAllById(ids);
    }

    @Override
    @Transactional
    public void check(Set<String> ids, String userId) {
        Function funcAnno = this.getClass().getAnnotation(Function.class);//获取当前调用者service上的@Function注解
        if(funcAnno!=null){//如果code不为空，则表示审核受限制，需要从权限表中查询授权信息，并验证；如果code为空，则表示不受权限验证限制
            String code = funcAnno.code();
            if(null!=code&&!"".equals(code)){//通过code到t_function_config表中查询权限信息
                //TODO
            }
        }
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] fds = tClass.getDeclaredFields();
        fds = getSuperClassFields(fds,tClass);
        Specification<T> specification = getSpecification(ids,fds,true);
        List<T> list = this.getBaseRepository().findByCondition(specification);
        if (list!=null&&list.size()>0) {
            for(int i=0;i<list.size();i++){
                T t = list.get(i);
                for(int j=0;j<fds.length;j++){
                    Field f = fds[j];
                    f.setAccessible(true);
                    try {
                        boolean fieldHasAnno = f.isAnnotationPresent(Column.class);
                        if (fieldHasAnno) {
                            Column column = f.getAnnotation(Column.class);
                            if(CheckableConstants.FCHECK_STATE.equals(column.name())){
                                f.set(t,true);
                            }else if(CheckableConstants.FCHECKER_ID.equals(column.name())){
                                f.set(t,userId);
                            }else if(CheckableConstants.FCHECK_TIME.equals(column.name())){
                                f.set(t,new Date());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.getBaseRepository().saveAll(list);
    }

    @Override
    @Transactional
    public void unCheck(Set<String> ids, String userId) {
        Function funcAnno = this.getClass().getAnnotation(Function.class);//获取当前调用者service上的@Function注解
        if(funcAnno!=null){//如果code不为空，则表示反审核受限制，需要从权限表中查询授权信息，并验证；如果code为空，则表示不受限制
            String code = funcAnno.code();
            if(null!=code&&!"".equals(code)){//通过code到t_function_config表中查询权限信息
                //TODO
            }
        }
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] fds = tClass.getDeclaredFields();
        fds = getSuperClassFields(fds,tClass);
        Specification<T> specification = getSpecification(ids,fds,false);
        List<T> list = this.getBaseRepository().findByCondition(specification);
        if (list!=null&&list.size()>0) {
            for(int i=0;i<list.size();i++){
                T t = list.get(i);
                for(int j=0;j<fds.length;j++){
                    Field f = fds[j];
                    f.setAccessible(true);
                    try {
                        boolean fieldHasAnno = f.isAnnotationPresent(Column.class);
                        if (fieldHasAnno) {
                            Column column = f.getAnnotation(Column.class);
                            if(CheckableConstants.FCHECK_STATE.equals(column.name())){
                                f.set(t,false);
                            }else if(CheckableConstants.FCHECKER_ID.equals(column.name())){
                                f.set(t,userId);
                            }else if(CheckableConstants.FCHECK_TIME.equals(column.name())){
                                f.set(t,new Date());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.getBaseRepository().saveAll(list);
    }

    //获取父类的所有字段
    private static Field[] getSuperClassFields(Field[] tableFields, Class<?> clazz) {
        Class<?> superClazz = clazz.getSuperclass();
        Field[] c = null;
        if (superClazz.equals(Object.class)) {
            c = tableFields;
        }else{
            Field[] tableSuperFields = superClazz.getDeclaredFields();
            c = new Field[tableFields.length + tableSuperFields.length];
            System.arraycopy(tableFields, 0, c, 0, tableFields.length);
            System.arraycopy(tableSuperFields, 0, c, tableFields.length, tableSuperFields.length);
            c = getSuperClassFields(c, superClazz);
        }
        return c;
    }

    /**
     * 审核反审核查询条件构造
     * @param ids
     * @param fields
     * @param check true表示审核 false表示反审核
     * @return
     */
    private Specification<T> getSpecification(Set<String> ids, Field[] fields, Boolean check){
        Specification<T> specification = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                String idField = "";
                String deleteField = "";
                String checkField = "";
                if(fields!=null&&fields.length>0){
                    for(int i=0;i<fields.length;i++){
                        Field f  = fields[i];
                        //如果此属性为非public属性的情况时，需要设置属性可达，否则会抛出IllegalAccessException异常
                        f.setAccessible(true);
                        boolean hasIdAnno = f.isAnnotationPresent(Id.class);
                        if(hasIdAnno){
                            idField = f.getName();
                        }

                        boolean fieldHasAnno = f.isAnnotationPresent(Column.class);
                        if (fieldHasAnno) {
                            Column column = f.getAnnotation(Column.class);
                            if ("FDELETE_STATE".equals(column.name())) {
                                deleteField = f.getName();
                            }else if(CheckableConstants.FCHECK_STATE.equals(column.name())){
                                checkField = f.getName();
                            }
                        }
                    }
                }

                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(((CriteriaBuilderImpl) cb).in(root.get(idField), ids));
                predicates.add(cb.isFalse(root.<Boolean>get(deleteField)));
                if(check){//审核  查询所有未审核数据
                    predicates.add(cb.isFalse(root.<Boolean>get(checkField)));
                }else{//反审核  查询所有已审核数据
                    predicates.add(cb.isTrue(root.<Boolean>get(checkField)));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        return specification;
    }
}
