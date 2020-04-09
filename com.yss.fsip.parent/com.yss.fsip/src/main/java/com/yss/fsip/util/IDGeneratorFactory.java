package com.yss.fsip.util;

import com.yss.fsip.common.util.id.IDGenerator;
import com.yss.fsip.common.util.id.NUIDGenerator;

/**
 * IDGenerator factory
 * @author LSP
 *
 */
public class IDGeneratorFactory {
	
	private static IDGenerator idGenerator;
	private final static Object lock = new Object();

	/**
	 * 获取IDGenerator 对象
	 * @return
	 */
	public static IDGenerator getIDGenerator() {
		if(idGenerator == null) {
			synchronized(lock) {
				if(idGenerator == null) {
					idGenerator = BeanUtil.getBean(IDGenerator.class);
					if(idGenerator == null) {
						idGenerator = new NUIDGenerator();
					}
				}
			}
		}
		return idGenerator;
	}
}
