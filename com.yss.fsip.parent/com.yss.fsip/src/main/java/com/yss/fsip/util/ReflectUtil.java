package com.yss.fsip.util;

import com.yss.fsip.annotations.UniqueValidate;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射操作工具类
 *
 * @author jingminy
 * @date 2020/1/20 14:06
 **/
public class ReflectUtil {
    /**
     * 获取类所有属性（包括父类的属性）
     *
     * @param object
     * @return 获取类的所有属性字段数组（包括父类的属性）
     * @author jingminy
     * @date 2019/12/30 11:25
     */
    private static Field[] getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 获取id字段
     *
     * @author jingminy
     * @date 2020/1/17 17:14
     */
    public static String getIdField(Object object) {
        return getFieldByAnnotation(object, Id.class);
    }

    /**
     * 获取删除标识字段
     *
     * @author jingminy
     * @date 2020/1/17 17:14
     */
    public static String getDeleteField(Object object) {
        String result = null;
        Field[] fields = getAllFields(object);
        for (Field field : fields) {
            boolean fieldHasAnno = field.isAnnotationPresent(Column.class);
            // 如果该字段中有标注IdMappingTarget 说明是要填写描述信息的属性
            if (fieldHasAnno) {
                Column column = field.getAnnotation(Column.class);
                if ("FDELETE_STATE".equals(column.name())) {
                    result = field.getName();
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 获取字段值
     *
     * @param object
     * @param field
     * @return
     */
    public static Object getFieldValue(Object object, String field) {
        Method[] m = object.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + field).toLowerCase().equals(m[i].getName().toLowerCase())) {
                try {
                    return m[i].invoke(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 根据属性，获取get方法
     *
     * @param ob   对象
     * @param name 属性名
     * @return
     * @throws Exception
     */
    public static Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }

    /**
     * 根据属性，获取方法返回值
     *
     * @return
     * @throws Exception
     */
    public static UniqueValidate getUniqueAnnotation(Method method) {
        UniqueValidate result = null;
        Object annotation = method.getAnnotation(UniqueValidate.class);
        if (annotation == null) {
            return result;
        }
        result = annotation == null ? null : (UniqueValidate) annotation;
        return result;
    }

    /**
     * 根据属性，获取方法返回值
     *
     * @return
     * @throws Exception
     */
    public static Method getMethod(Class clazz, String methodName) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Method result = null;
        Method[] methods = clazz.getMethods();
        if (methods == null || methods.length == 0) {
            return null;
        }
        for (Method method : methods) {
            String _methodName = method.getName();
            if (!methodName.equals(_methodName)) {
                continue;
            }
            return method;
        }
        return result;
    }

    /**
     * 根据属性，获取方法返回值
     *
     * @return
     * @throws Exception
     */
    public static Object getMethodReturnValue(Class clazz, Method method, Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return method.invoke(clazz.newInstance(), args);
    }

    /**
     * 根据属性，获取方法返回值
     *
     * @return
     * @throws Exception
     */
    public static String getMethodReturnString(Class clazz, Method method) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object object = getMethodReturnValue(clazz, method, null);
        return object == null ? null : (String) object;
    }

    /**
     * 获取分组名称
     *
     * @param object
     * @param unique
     * @return
     * @throws Exception
     */
    public static String getGroupNameValue(Object object, UniqueValidate unique) throws Exception {
        // 注解里有则使用注解中的配置
        String groupCode = unique.group();
        String groupNameCode = unique.groupNameCode();
        if (StringUtils.isEmpty(groupNameCode)) {
            return groupCode;
        }
        String[] groupNameValueCodes = groupNameCode.split("\\.");
        Object obj = object;
        for (String each : groupNameValueCodes) {
            obj = getGetMethod(obj, each);
        }
        return obj == null ? groupNameCode : (String) obj;
    }

    /**
     * 根据属性，获取方法返回值
     *
     * @return
     * @throws Exception
     */
    public static Object getMethodReturnValue(Class clazz, String methodName) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String result = null;
        Method[] methods = clazz.getMethods();
        if (methods == null || methods.length == 0) {
            return result;
        }
        for (Method method : methods) {
            String _methodName = method.getName();
            if (!methodName.equals(_methodName)) {
                continue;
            }
            return method.invoke(clazz.newInstance(), null);
        }
        return result;
    }

    /**
     * 根据注解获取属性字段
     *
     * @param object
     * @param annotation
     * @return
     */
    public static String getFieldByAnnotation(Object object, Class annotation) {

        String result = null;
        Field[] fields = getAllFields(object);
        for (Field field : fields) {
            boolean fieldHasAnno = field.isAnnotationPresent(annotation);
            // 如果该字段中有标注IdMappingTarget 说明是要填写描述信息的属性
            if (fieldHasAnno) {
                result = field.getName();
                return result;
            }
        }
        return result;
    }
}
