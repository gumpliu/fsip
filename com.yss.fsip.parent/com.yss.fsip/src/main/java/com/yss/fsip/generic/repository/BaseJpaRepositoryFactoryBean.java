package com.yss.fsip.generic.repository;

import com.yss.fsip.generic.repository.impl.BaseJpaRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class BaseJpaRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable> extends JpaRepositoryFactoryBean<R, T, ID> {

    public BaseJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        BaseRepositoryFactory baseRepositoryFactory  =  new BaseRepositoryFactory(entityManager);
        baseRepositoryFactory.addRepositoryProxyPostProcessor(new SecurecyPostProcessor());
        return baseRepositoryFactory;
    }

    //创建一个内部类，该类不用在外部访问
    private static class BaseRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

        public BaseRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        //设置具体的实现类是BaseRepositoryImpl
        @SuppressWarnings("unchecked")
        @Override
        protected JpaRepositoryImplementation<T, ID> getTargetRepository(RepositoryInformation information,
        		EntityManager entityManager) {
            return new BaseJpaRepositoryImpl<T, ID>((Class<T>) information.getDomainType(), entityManager);
        }

        //设置具体的实现类的class
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseJpaRepositoryImpl.class;
        }
    }
}