package com.yss.fsip.util.idmapping;

/**
 * 数据绑定接口
 *
 * @Author: gumpLiu
 * @Date: 2019-11-14 11:27
 */
public interface MappingDataBinding {

    /***
     * 通过type,value获取需要转换的值
     * @param sourceCode 被转换属性名称
     * @param targetCode 转出成属性名称
     * @param value
     * @return
     */
    public String getData(String sourceCode,String targetCode, String value);

}
