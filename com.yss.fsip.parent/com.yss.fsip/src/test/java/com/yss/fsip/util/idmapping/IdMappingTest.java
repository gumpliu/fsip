package com.yss.fsip.util.idmapping;

import com.yss.fsip.PackageAllScaner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= PackageAllScaner.class)
public class IdMappingTest {
    @Test
    public void idMappingTest(){

        SourceObject sourceObject = new SourceObject();
        sourceObject.setId("001");
        sourceObject.setAge("20");
        SourceObject sourceObject1 = new SourceObject();
        sourceObject1.setId("002");
        sourceObject1.setAge("21");
        SourceObject sourceObject2 = new SourceObject();
        sourceObject2.setId("002");
        sourceObject2.setAge("22");

        List<SourceObject> list = new ArrayList<>();
        list.add(sourceObject);
        list.add(sourceObject1);
        list.add(sourceObject2);

        List<SourceObject> lists =  IdMappingUtil.change(list);
        for(SourceObject so : lists){
           Assert.notNull(so.getName(),"name 不能为空");
        }

    }  
   
}  