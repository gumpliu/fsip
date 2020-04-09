//package com.yss.fsip.generic.repository.impl;
//
//import com.google.common.collect.Lists;
//import com.yss.fsip.generic.repository.BaseRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.keyvalue.core.KeyValueOperations;
//import org.springframework.data.keyvalue.repository.support.SimpleKeyValueRepository;
//import org.springframework.data.repository.core.EntityInformation;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Set;
//
//public class BaseRedisRepositoryImpl<T, ID extends Serializable> extends SimpleKeyValueRepository<T, ID> implements BaseRepository<T, ID> {
//
//    public BaseRedisRepositoryImpl(EntityInformation<T, ID> metadata, KeyValueOperations operations) {
//        super(metadata, operations);
//    }
//
//    @Override
//    public Page<T> findPage(int courrentPage, int pageSize) {
//
//        Pageable pageable =  PageRequest.of(courrentPage, pageSize);
//
//        return findAll(pageable);
//    }
//
//    @Override
//    public List<T> findAll(){
//        return Lists.newArrayList(super.findAll());
//    }
//
//    @Override
//    public List<T> findAll(Sort sort){
//
//        return Lists.newArrayList(super.findAll(sort));
//    }
//
//    @Override
//    public List<T> findAllById(Iterable<ID> ids){
//
//        return Lists.newArrayList(super.findAllById(ids));
//    }
//
//    @Override
//    public List<T> findByCondition(Specification<T> spec) {
//        return null;
//    }
//
//    @Override
//    public Page<T> findPageByCondition(Specification<T> spec, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> List<S> check(Set<String> ids, String userId, Class<T> tClass) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> List<S> unCheck(Set<String> ids, String userId, Class<T> tClass) {
//        return null;
//    }
//}