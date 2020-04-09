package com.yss.fsip.util.idmapping;

import com.yss.fsip.util.BeanUtil;

/**
 * 获取MappingDataBinding实例
 */
public class MappingDataBindingFactory {

    /**
     * 从ioc容器中获取bean
     * @param clazz
     * @return
     */
    public static MappingDataBinding getInstance(Class<MappingDataBinding> clazz) {
        return BeanUtil.getBean(clazz);
    }

}
