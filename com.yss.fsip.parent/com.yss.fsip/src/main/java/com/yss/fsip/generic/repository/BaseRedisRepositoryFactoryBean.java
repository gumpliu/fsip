//package com.yss.fsip.generic.repository;
//
//import com.yss.fsip.generic.repository.impl.BaseRedisRepositoryImpl;
//import org.springframework.data.keyvalue.core.KeyValueOperations;
//import org.springframework.data.redis.repository.support.RedisRepositoryFactory;
//import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;
//import org.springframework.data.repository.Repository;
//import org.springframework.data.repository.core.EntityInformation;
//import org.springframework.data.repository.core.RepositoryInformation;
//import org.springframework.data.repository.core.RepositoryMetadata;
//import org.springframework.data.repository.query.RepositoryQuery;
//import org.springframework.data.repository.query.parser.AbstractQueryCreator;
//
//import java.io.Serializable;
//
//public class BaseRedisRepositoryFactoryBean<R extends Repository<T, ID>, T, ID extends Serializable> extends RedisRepositoryFactoryBean<R, T, ID> {
//    public BaseRedisRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
//        super(repositoryInterface);
//    }
//
//    @Override
//    protected RedisRepositoryFactory createRepositoryFactory(KeyValueOperations operations,
//                                                             Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
//
//        return new BaseRedisRepositoryFactory(operations);
//    }
//
//    //创建一个内部类，该类不用在外部访问
//    private static class BaseRedisRepositoryFactory<T, ID extends Serializable> extends RedisRepositoryFactory {
//
//        private final KeyValueOperations keyValueOperations;
//
//        public BaseRedisRepositoryFactory(KeyValueOperations keyValueOperations) {
//            super(keyValueOperations);
//            this.keyValueOperations = keyValueOperations;
//        }
//
//
//        //设置具体的实现类是BaseRepositoryImpl
//        @SuppressWarnings("unchecked")
//        @Override
//        protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
//            EntityInformation<?, ?> entityInformation = this.getEntityInformation(repositoryInformation.getDomainType());
//            return new BaseRedisRepositoryImpl(entityInformation, keyValueOperations);
//        }
//
//        @Override
//        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
//            return BaseRedisRepositoryImpl.class;
//        }
//
//    }
//
//}