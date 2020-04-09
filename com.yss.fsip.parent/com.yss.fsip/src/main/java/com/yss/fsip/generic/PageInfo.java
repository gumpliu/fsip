package com.yss.fsip.generic;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 分页实体
 * @Author gumpLiu
 * @Date 2019-12-14
 * @Version V1.0
 **/
public class PageInfo<T> implements Serializable {
    //当前页
    private int pageNumber;
    //每页的数量
    private int pageSize;
    //总记录数
    private long total;
    //总页数
    private int pages;
    //结果集
    private List<T> list;


    public PageInfo(){}

    public PageInfo(Page page){
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.total = page.getTotalElements();
        this.pages = page.getTotalPages();
        this.list = page.getContent();
    }

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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
