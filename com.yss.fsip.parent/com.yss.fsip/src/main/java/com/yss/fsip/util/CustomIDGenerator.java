package com.yss.fsip.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class CustomIDGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Object id = IDGeneratorFactory.getIDGenerator().nextId();
        return (Serializable) id;
    }

}
