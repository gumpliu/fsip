package com.yss.fsip.common.util;

import com.yss.fsip.common.annotation.FSIPBeanPropertyTarget;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 实体类拷贝工具类<br/>
 * 说明：此工具类支持深度拷贝
 *
 * @author jingminy
 * @date 2019/12/16 15:00
 */
public class FSIPBeanUtil {

    /**
     * 源实体对象转目标实体对象
     *
     * @param source      源实体对象
     * @param targetClass 目标实体class
     * @param <T>         目标实体类型
     * @return 目标实体
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        Object instantiate = BeanUtils.instantiate(targetClass);
        convertToBean(source, instantiate, null);
        return (T) instantiate;
    }

    /**
     * 源实体对象转目标实体对象(批量)
     *
     * @param sources     源实体对象集合
     * @param targetClass 目标实体class
     * @param <T>         目标实体类型
     * @return 目标实体
     */
    public static <T> List<T> convertList(List<?> sources, Class<T> targetClass) {
        List<T> targets = new ArrayList<T>();
        for (Object source : sources) {
            //使用工具类将tuple转为对象
            T target = convert(source, targetClass);
            targets.add(target);
        }
        ;
        return targets;
    }

    /**
     * tuple转实体对象
     *
     * @param source           tuple对象
     * @param targetClass      目标实体class
     * @param <T>              目标实体类型
     * @param ignoreProperties 要忽略的属性
     * @return 目标实体
     */
    public static <T> T convert(Class<T> source, Class<T> targetClass, String... ignoreProperties) {
        Object targetObj = BeanUtils.instantiate(targetClass);
        convertToBean(source, targetObj, ignoreProperties);
        return (T) targetObj;
    }

    /**
     * 把tuple中属性名相同的值复制到实体中
     *
     * @param source tuple对象
     * @param target 目标对象实例
     */
    public static void convertToBean(Object source, Object target) {
        convertToBean(source, target, null);
    }

    /**
     * 把tuple中属性名相同的值复制到实体中
     *
     * @param source           tuple对象
     * @param target           目标对象实例
     * @param ignoreProperties 要忽略的属性
     */
    public static void convertToBean(Object source, Object target, String... ignoreProperties) {
        //目标class
        Class<?> sourceClass = source.getClass();
        Map<String, PropertyDescriptor> sourcePdMap = getSourcePdMap(sourceClass);

        //目标class
        Class<?> actualEditable = target.getClass();
        //获取目标类的属性信息
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        //忽略列表
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        Map<String, String> propertyTargetMap = new HashMap<String, String>();

        //遍历属性节点信息
        for (PropertyDescriptor targetPd : targetPds) {
            //获取set方法
            Method writeMethod = targetPd.getWriteMethod();
            //判断字段是否可以set
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                try {
                    //获取source节点对应的属性
                    String propertyName = getPropertyName(propertyTargetMap, actualEditable, targetPd);
                    PropertyDescriptor sourcePd = sourcePdMap.get(propertyName);
                    if (sourcePd == null) {
                        continue;
                    }
                    //获取set方法
                    Method readMethod = sourcePd.getReadMethod();

                    //判断source属性是否private
                    if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    //写入target
                    Object value = readMethod.invoke(source);
                    if (value != null) {

                        //判断target属性是否private
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        //写入target
                        writeMethod.invoke(target, value);

                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    throw new FatalBeanException(
                            "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                }
            }
        }
    }

    private static Map<String, PropertyDescriptor> getSourcePdMap(Class<?> clazz) {
        Map<String, PropertyDescriptor> sourcePdMap = new HashMap<String, PropertyDescriptor>();
        while (clazz != null) {
            //获取目标类的属性信息
            PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(clazz);
            for (int i = 0; i < sourcePds.length; i++) {
                PropertyDescriptor pd = sourcePds[i];
                sourcePdMap.put(pd.getName(), pd);
            }
            clazz = clazz.getSuperclass();
        }
        return sourcePdMap;
    }

    private static String getPropertyName(Map<String, String> propertyTargetMap, Class<?> clazz, PropertyDescriptor targetPd) {
        String propertyName = targetPd.getName();
        String result = propertyTargetMap.get(propertyName);
        if (result != null) {
            return result;
        }
        Field field = getField(clazz, propertyName);
        FSIPBeanPropertyTarget propertyTarget = field.getAnnotation(FSIPBeanPropertyTarget.class);
        if (propertyTarget != null) {
            result = propertyTarget.target();
        } else {
            result = propertyName;
        }
        propertyTargetMap.put(propertyName, result);
        return result;
    }

    private static Field getField(Class<?> clazz, String propertyName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                return getField(clazz, propertyName);
            } else {
                e.printStackTrace();
            }
        }
        return field;
    }

}
