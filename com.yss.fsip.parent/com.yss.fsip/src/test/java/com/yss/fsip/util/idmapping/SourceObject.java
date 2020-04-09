package com.yss.fsip.util.idmapping;

import com.yss.fsip.annotations.IdMappingTarget;

/**
 * 测试源 数据
 * @Author: gumpLiu
 * @Date: 2019-11-18 11:01
 */
public class SourceObject {


    private String id;
    /**
     * 根据type值user找到UserMappingDataBinding
     */
    @IdMappingTarget(fromCode = "id", mappingDataBinding = UserMappingDataBinding.class)
    private String name;
    private String age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
