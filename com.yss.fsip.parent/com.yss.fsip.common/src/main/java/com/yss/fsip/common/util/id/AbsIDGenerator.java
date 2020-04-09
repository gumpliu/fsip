package com.yss.fsip.common.util.id;

/**
 * ID生成器基类。
 * @author lenglinyong
 * @version 1.0, 2013-1-11
 * @since 1.0, 2013-1-11
 */
public abstract class AbsIDGenerator implements IDGenerator {

    /**
     * 生成单个id
     * @return
     */
	
    protected abstract String generate(int order);
    
    /**
     * 获取一个id
     */
    public abstract String nextId();
}
