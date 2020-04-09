package com.yss.fsip.generic.repository.impl;

import com.yss.fsip.annotations.UniqueValidate;
import com.yss.fsip.generic.entity.UniqueVerifiableEntity;
import com.yss.fsip.constants.FSIPErrorCode;
import com.yss.fsip.exception.FSIPRuntimeException;
import com.yss.fsip.generic.repository.BaseRepository;
import com.yss.fsip.util.ReflectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

public class BaseJpaRepositoryImpl<T, TD extends Serializable> extends SimpleJpaRepository<T, TD> implements BaseRepository<T, TD> {

    private final EntityManager entityManager; //父类没有不带参数的构造方法，这里手动构造父类

    public BaseJpaRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> findPage(int courrentPage, int pageSize) {

        Pageable pageable = PageRequest.of(courrentPage, pageSize);

        return findAll((Specification<T>) null, pageable);
    }

    @Override
    public List<T> findByCondition(Specification<T> spec) {
        return super.findAll(spec);
    }

    @Override
    public Page<T> findPageByCondition(Specification<T> spec, Pageable pageable) {
        return super.findAll(spec, pageable);
    }

    @Override
    public <S extends T> S save(S entity) {
        validateUnique(entity);
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        validateUnique(entity);
        return super.saveAndFlush(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        for(S entity : entities) {
            validateUnique(entity);
        }
        return super.saveAll(entities);
    }

    /**
     * 保存自动校验数据唯一性<br/>
     * 唯一性校验规则：<br/>
     * 一、实体类实现UniqueVerifiableEntity接口，实现getUniqueProperty方法，设置需要做唯一性校验的属性，<br/>
     * 唯一性校验属性设置规则：|为或者，&为与，例如：categoryId&code|categoryId&name表示同一字典类目下不允许存在相同数据字典名称或者相同数据字典编码的数据
     * 二、Repository继承自BaseRepositoryImpl
     * 会进行唯一性校验的方法：save、saveAndFlush、saveAll
     *
     * @param entity 待校验Entity实体
     */
    private void validateUnique(T entity) {
        try {
            // 只有实现了UniqueVerifiableEntity才会走唯一性校验逻辑
            if (!(entity instanceof UniqueVerifiableEntity)) {
                return;
            }
            Class clazz = entity.getClass();
            String uniqueMethod = "getUniqueProperty";
            Method getUniqueProperty = ReflectUtil.getMethod(clazz, uniqueMethod);
            String uniqueProperty = ReflectUtil.getMethodReturnString(clazz, getUniqueProperty);

            // 无效的唯一性校验配置
            if (StringUtils.isEmpty(uniqueProperty)) {
                FSIPErrorCode errorCode = FSIPErrorCode.INVALID_UNIQUE_CONFIG;
                throw new FSIPRuntimeException(errorCode.getErrorCode(), MessageFormat.format(errorCode.getErrorDesc(), entity.getClass().getName()));
            }

            // Map<field属性,field值>：唯一性校验失败时编码到名称的转换时使用
            Map<String, Object> uniquePropertyMap = new HashMap<String, Object>();

            // 获取ID字段（@ID标注的字段）和删除字段(@Delete标注的字段)
            String idField = ReflectUtil.getIdField(entity);
            Object idFieldValueObject = ReflectUtil.getFieldValue(entity, idField);
            String idFieldValue = idFieldValueObject == null ? "" : (String) idFieldValueObject;

            String deleteField = ReflectUtil.getDeleteField(entity);

            // 注解里有则使用注解中的配置
            UniqueValidate unique = ReflectUtil.getUniqueAnnotation(getUniqueProperty);

            // 分组信息
            String groupField = unique.group() == null ? "" : unique.group();
            String groupFieldValue = "";
            String groupName = "";
            String groupNameValue = "";
            if (StringUtils.isNotEmpty(groupField)) {
                Object groupFieldValueObject = ReflectUtil.getFieldValue(entity, groupField);
                groupFieldValue = groupFieldValueObject == null ? "" : (String) groupFieldValueObject;
                groupName = unique.groupName() == null ? groupField : unique.groupName();
                groupNameValue = ReflectUtil.getGroupNameValue(entity, unique);
            }

            // 唯一性校验规则，getUniqueProperty方法中的返回值配置
            String uniquePropertyCode = uniqueProperty;

            //TODO 前端国际化后去掉此处编码到名称的转换   start
            String uniquePropertyName = uniqueProperty;
            String _uniquePropertyName = unique.name();
            if (StringUtils.isNotEmpty(_uniquePropertyName)) {
                uniquePropertyName = _uniquePropertyName;
            }
            //TODO 前端国际化后去掉此处编码到名称的转换   end

            // 分割唯一性校验规则
            String orRegex = "\\|";
            String[] ors = uniqueProperty.split(orRegex);
            String[] displayValues = uniquePropertyCode.split(orRegex);
            String[] displayNames = uniquePropertyName.split(orRegex);
            for (int i = 0; i < ors.length; i++) {
                String ruleFields = ors[i];
                String finalGroupFieldValue = groupFieldValue;
                final Specification<T> specification = new Specification<T>() {
                    @Override
                    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<Predicate>();
                        String[] fields = ruleFields.split("&");
                        Predicate p = null;
                        for (int j = 0; j < fields.length; j++) {
                            String field = fields[j];
                            Object fieldValue = ReflectUtil.getFieldValue(entity, field);
                            uniquePropertyMap.put(field, fieldValue);
                            Predicate p2 = cb.equal(root.<String>get(field), fieldValue);
                            if (j == 0) {
                                p = p2;
                            } else {
                                p = cb.and(p, p2);
                            }
                        }
                        if (StringUtils.isNotEmpty(groupField)) {
                            p = cb.and(p, cb.equal(root.<String>get(groupField), finalGroupFieldValue));
                        }
                        if (StringUtils.isNotEmpty(idFieldValue)) {
                            p = cb.and(p, cb.notEqual(root.<String>get(idField), idFieldValue));
                        }
                        p = cb.and(p, cb.isFalse(root.<Boolean>get(deleteField)));

                        predicates.add(p);
                        return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
                    }
                };

                List<T> list = this.findAll(specification);
                if (CollectionUtils.isEmpty(list)) {
                    continue;
                }
                String displayValue = displayValues[i];
                String displayName = displayNames[i];

                //TODO 前端国际化后去掉此处编码到名称的转换   start
                Set<String> fields = uniquePropertyMap.keySet();
                for (String field : fields) {
                    displayValue = displayValue.replaceAll(field, uniquePropertyMap.get(field).toString());
                }
                //TODO 前端国际化后去掉此处编码到名称的转换   end

                if (StringUtils.isNotEmpty(groupField)) {
                    FSIPErrorCode errorCode = FSIPErrorCode.SAVE_GROUP_UNIQUE_ERR;
                    throw new FSIPRuntimeException(errorCode.getErrorCode(), MessageFormat.format(errorCode.getErrorDesc(), groupName,  groupNameValue, displayName, displayValue));
                } else {
                    FSIPErrorCode errorCode = FSIPErrorCode.SAVE_UNIQUE_ERR;
                    throw new FSIPRuntimeException(errorCode.getErrorCode(), MessageFormat.format(errorCode.getErrorDesc(), displayName, displayValue));
                }
            }
        } catch (FSIPRuntimeException fsip) {
            throw fsip;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FSIPRuntimeException(e);
        }
    }

}