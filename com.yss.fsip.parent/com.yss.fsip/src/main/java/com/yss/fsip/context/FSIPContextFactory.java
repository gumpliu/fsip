package com.yss.fsip.context;


/**
 * 上下文工厂类
 */
public abstract class FSIPContextFactory {

    protected static InheritableThreadLocal<FSIPContext> contexts = new InheritableThreadLocal<FSIPContext>();

    public static FSIPContext getContext() {
        return contexts.get();
    }
    public static void remove() {
        contexts.remove();
    }


    /**
     * 创建上下文信息
     *
     * @return
     */
    public abstract FSIPContext createContext();
}
