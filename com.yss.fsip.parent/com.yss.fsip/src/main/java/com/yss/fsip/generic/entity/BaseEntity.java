package com.yss.fsip.generic.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity extends IDEntity {

    /**
     * 字段名称_创建人ID
     */
    public static final String FIELD_NAME_CREATORID = "creatorId";
    /**
     * 字段名称_创建时间
     */
    public static final String FIELD_NAME_CREATETIME = "createTime";
    /**
     * 字段名称_修改人ID
     */
    public static final String FIELD_NAME_LASTEDITORID = "lastEditorId";
    /**
     * 字段名称_修改时间
     */
    public static final String FIELD_NAME_LASTEDITTIME = "lastEditTime";

    /**
     * 创建人
     */
    @Column(name = "FCREATOR_ID")
    @CreatedBy
    private String creatorId;

    /**
     * 创建时间
     */
    @Column(name = "FCREATE_TIME")
    @CreatedDate
    private Date createTime;

    /**
     * 修改人
     */
    @Column(name = "FLAST_EDITOR_ID")
    @LastModifiedBy
    private String lastEditorId;

    /**
     * 修改时间
     */
    @Column(name = "FLAST_EDIT_TIME")
    @LastModifiedDate
    private Date lastEditTime;

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(String lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
