package com.yss.fsip.generic;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 需要分页查询的dto基类
 *
 * @author jingminy
 * @date 2019/12/26 15:46
 */
public class BasePageQuery {

    /**
     * 当前页
     *
     * @author jingminy
     * @date 2019/12/17 10:40
     */
    public int pageNumber;

    /**
     * 每页显示条数
     *
     * @author jingminy
     * @date 2019/12/17 10:40
     */
    public int pageSize;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Pageable getPage() {
        return new PageRequest(pageNumber, pageSize);
    }

    public Pageable getPage(Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }
}
