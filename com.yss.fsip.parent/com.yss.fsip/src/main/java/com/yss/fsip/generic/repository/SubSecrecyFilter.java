package com.yss.fsip.generic.repository;

import ch.qos.logback.classic.Logger;
import com.yss.fsip.annotations.Function;
import com.yss.fsip.common.util.StringUtil;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.generic.entity.BaseEntity;
import com.yss.fsip.log.core.BatchDBLoggerAppender;
import com.yss.fsip.log.core.config.AuditLogProperties;
import com.yss.fsip.log.core.impl.DBLogger;
import com.yss.fsip.util.BeanUtil;
import org.aopalliance.intercept.MethodInvocation;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SubSecrecyFilter {

    private static AtomicBoolean isStart = new AtomicBoolean(false);

    public static Object dofilter(MethodInvocation Invocation) throws Throwable{

        Object obj=Invocation.proceed();
        //批处理的方法中上下文不会记录user信息，因此批处理不记录日志
        Function function=null;
        StackTraceElement stack = null;
        Class<?> clazz=null;
        //查询方法不放入日志
        if(Invocation.getMethod().getName().startsWith("query") || Invocation.getMethod().getName().startsWith("find")){
            return obj;
        }
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            // 通过类名找到堆栈对象
            if (stacks[i].getClassName().contains("ServiceImpl")) {
                stack = stacks[i];
                break;
            }
        }
        if(stack!=null){
            clazz=Class.forName(stack.getClassName());
            function=clazz.getAnnotation(Function.class);
            //审计日志不入库
            if(StringUtil.isNotEmpty(function.code())){
                if(function.code().equals("auditlogImpl")){
                    return obj;
                }
            }
        }
        if(FSIPContextFactory.getContext().getUserId()!=null){
            //当配置的参数是false时，也不记录日志
            AuditLogProperties auditLogProperties=BeanUtil.getBean(AuditLogProperties.class);
            if(auditLogProperties.getRequest()==false){
                return obj;
            }
            Object[] objects=Invocation.getArguments();
            Object logParam=null;
            if(objects!=null && objects.length>0){
                logParam=objects[0];
            }
            if(isStart.get()==false) {
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
                Logger logger = context.getLogger("bizLogger."+Invocation.getMethod().getDeclaringClass().getName());
                logger.setAdditive(false);
                BatchDBLoggerAppender dbAppender=new BatchDBLoggerAppender();
                dbAppender.start();
                logger.addAppender(dbAppender);
                logger.setAdditive(false);
                isStart.set(true);
            }
            DBLogger db=new DBLogger();
            db.success(Invocation.getMethod().getDeclaringClass(),Invocation.getMethod(),logParam,function,auditLogProperties.getApplicationName());
        }
        return obj;
    }
}

