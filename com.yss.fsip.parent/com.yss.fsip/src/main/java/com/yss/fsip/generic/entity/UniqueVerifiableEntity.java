package com.yss.fsip.generic.entity;

/**
 * 需要唯一性校验可以实现此接口<br/>
 * 通过getUniqueProperty方法设置需要做唯一性校验的字段：&为与，|为或
 *
 * @author jingminy
 * @date 2020/1/17 17:24
 */
public interface UniqueVerifiableEntity {

    /**
     * 设置唯一属性，规则：|为或者，&为与，例如：categoryId&code|categoryId&name表示同一字典类目下不允许存在相同数据字典名称或者相同数据字典编码的数据
     *
     * @author jingminy
     * @date 2020/1/17 17:27
     */
    public String getUniqueProperty();
}
