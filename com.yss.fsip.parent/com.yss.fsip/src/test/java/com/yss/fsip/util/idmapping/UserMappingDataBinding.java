package com.yss.fsip.util.idmapping;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * mappingDataBinding Test
 *
 * @Author: gumpLiu
 * @Date: 2019-11-18 10:56
 */
@Component
public class UserMappingDataBinding implements  MappingDataBinding {

    private static final Map<String, String> map = new HashMap<String, String>();

    static {
        map.put("001", "张三");
        map.put("002", "李四");
        map.put("003", "王五");

    }

    @Override
    public String getData(String sourceCode,String targetCode, String value) {
        return map.get(value);
    }
}
