package com.yss.fsip.generic.entity;

import com.yss.fsip.constants.CheckableConstants;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class CheckableEntity extends BaseEntity {

    /**
     * 字段名称_审核人ID
     */
    public static final String FIELD_NAME_CHECKERID = "checkerId";
    /**
     * 字段名称_审核状态
     */
    public static final String FIELD_NAME_CHECKSTATE = "checkState";
    /**
     * 字段名称_审核时间
     */
    public static final String FIELD_NAME_CHECKTIME = "checkTime";

    /**
     * 审核人
     */
    @Column(name = CheckableConstants.FCHECKER_ID)
    private String checkerId;

    /**
     * 审核状态
     */
    @Column(name = CheckableConstants.FCHECK_STATE, nullable = false)
    private Boolean checkState;

    /**
     * 审核时间
     */
    @Column(name = CheckableConstants.FCHECK_TIME)
    private Date checkTime;

    public String getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public Boolean getCheckState() {
        return checkState;
    }

    public void setCheckState(Boolean checkState) {
        this.checkState = checkState;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }
}
