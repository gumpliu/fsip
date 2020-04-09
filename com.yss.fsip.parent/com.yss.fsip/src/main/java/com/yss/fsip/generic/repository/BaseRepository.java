package com.yss.fsip.generic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID>  extends PagingAndSortingRepository<T,ID> {

    int DEFULT_PAGE_SIZE = 20; //每页默认大小

    /**
     * 单表分页，每页显示20条数据
     *
     * @param pageNumber 当前页
     * @return
     */
    default Page<T> findPage(int pageNumber) {
        return findPage(pageNumber, DEFULT_PAGE_SIZE);
    }

    /**
     * 单表分页
     *
     * @param pageNumber 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    Page<T> findPage(int pageNumber, int pageSize);

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> findAll();


    /**
     * 查询所有排序
     *
     * @param sort
     * @return
     */
    List<T> findAll(Sort sort);

    /**
     * 查询指定ids
     * @param ids
     * @return
     */
    List<T> findAllById(Iterable<ID> ids);

    /**
     * 根据条件查询所有数据
     *
     * @param spec 查询条件
     * @return 列表数据
     */
    public List<T> findByCondition(@Nullable Specification<T> spec);

    /**
     * 根据条件分页查询
     *
     * @param spec 查询条件
     * @param pageable      分页参数
     * @return 带分页数据
     */
    public Page<T> findPageByCondition(@Nullable Specification<T> spec, Pageable pageable);

}
