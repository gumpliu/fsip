package com.yss.fsip.generic.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class IDEntity implements Serializable {

    /**
     * 字段名称_ID
     */
    public static final String FIELD_NAME_ID = "id";

    @Id
    @Column(name = "FID", unique = true, nullable = false)
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.yss.fsip.util.CustomIDGenerator")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
