package com.yss.fsip.generic.repository;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

public class SecurecyPostProcessor implements RepositoryProxyPostProcessor {
    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addAdvice(SecurecyAdvice.instance);
    }
    static enum SecurecyAdvice implements MethodInterceptor {
        instance;
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return SubSecrecyFilter.dofilter(invocation);
        }
    }
}
