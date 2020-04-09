package com.yss.fsip.util.idmapping;

import com.yss.fsip.annotations.IdMappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IdMapping数据转换工具类
 */
public class IdMappingUtil {

	private static Logger logger=LoggerFactory.getLogger(IdMappingUtil.class);
	/**
	 * 列表数据转换,只是将对象中某些需要转换的字段内容进行转换，
	 * 该方法主要满足对系统中已存在的对象，新增字段中有注解IdMappingTarget的字段进行翻译。
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> List<T> change(List<T> obj) throws RuntimeException {

		try {
			for (T tempSourceObj : obj) {

				objectInterchange(tempSourceObj, null);

			}
		} catch (Exception e) {
			logger.error("数据IdMapping转换错误:",e);
			throw new RuntimeException("数据IdMapping转换错误", e);
		}
		return obj;
	}

	/**
	 * 列表数据转换，从VO对象转到DTO对象
	 * 
	 * @param sourceList
	 * @param targetClass
	 * @return
	 * @throws RuntimeException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, V> List<V> change(List<T> sourceList, Class targetClass) throws RuntimeException {

		List<V> targetList = new ArrayList<>();

		try {
			for (T tempSourceObj : sourceList) {
				V tempTargetObj = (V) targetClass.newInstance();
				objectInterchange(tempSourceObj, tempTargetObj);
				targetList.add(tempTargetObj);
			}
		} catch (Exception e) {
			logger.error("数据IdMapping转换错误:",e);
			throw new RuntimeException("数据IdMapping转换错误", e);
		}
		return targetList;
	}

	/**
	 * 对象内部数据映射
	 *
	 * @param obj
	 *            待映射对象
	 * @return
	 * @throws RuntimeException
	 */
	public static Object change(Object obj) throws RuntimeException {

		try {

			objectInterchange(obj);

		} catch (Exception e) {
			logger.error("数据IdMapping转换错误:",e);
			throw new RuntimeException("数据IdMapping转换错误", e);
		}
		return obj;
	}

	/**
	 * 单个对象数据转换，只是将VO对象转换成DTO对象。
	 * 
	 * @param sourceObj
	 *            vo对象
	 * @param targetObj
	 *            DTO对象
	 * @return
	 * @throws RuntimeException
	 */
	public static <T, V> V change(T sourceObj, V targetObj) throws RuntimeException {

		try {

			objectInterchange(sourceObj);

		} catch (Exception e) {
			logger.error("数据IdMapping转换错误:",e);
			throw new RuntimeException("数据IdMapping转换错误", e);
		}
		return targetObj;
	}

	/**
	 * 单个对象转换
	 * 
	 * @param sourceObj
	 * @param targetObj
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static <V, T> void objectInterchange(T sourceObj, V targetObj)
		throws IntrospectionException, IllegalAccessException, InvocationTargetException {

		if (sourceObj == null) {
			return;
		}
		if (targetObj == null) {
			objectInterchange(sourceObj);
		}else{
			BeanUtils.copyProperties(sourceObj, targetObj);
			objectInterchange(targetObj);
		}

	}

	/**
	 * idMapping 数据转化
	 *
	 * @param sourceObj
	 *
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static void objectInterchange(Object sourceObj)
			throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor pd;
        Method rM;
        Method wM;
        String num;
        Field[] fields = getAllFields(sourceObj);
        for (Field field : fields) {
            boolean fieldHasAnno = field.isAnnotationPresent(IdMappingTarget.class);
            // 如果该字段中有标注IdMappingTarget 说明是要填写描述信息的属性
            if (fieldHasAnno) {
                IdMappingTarget fieldAnno = field.getAnnotation(IdMappingTarget.class);
                // 获取读取缓存缓存的key或db对应属性
                String sourceCode = fieldAnno.sourceCode();
				// 获取转换成db对应属性
				String targetCode = fieldAnno.targetCode();
                // 获取需要转换的数据所在的属性名称
                String idCol = fieldAnno.fromCode();

                Class mappingDataBinding = fieldAnno.mappingDataBinding();
                // 获取该字段是不是多个值需要转换成描述
//				String dataSplit = fieldAnno.dataSplit();

                // 获取该属性的元数据
                pd = new PropertyDescriptor(idCol, sourceObj.getClass());
				// 获得该属性读方法
                rM = pd.getReadMethod();
				// 获取到该需要转换的ID\code的值
                num = rM.invoke(sourceObj) != null ? String.valueOf(rM.invoke(sourceObj)) : "";

                pd = new PropertyDescriptor(field.getName(), sourceObj.getClass());
				// 获得填写描述信息属性写方法
                wM = pd.getWriteMethod();

                MappingDataBinding mdb = MappingDataBindingFactory.getInstance(mappingDataBinding);

                String name = "";
                if (mdb != null){
                    name = mdb.getData(sourceCode, targetCode, num);
                }
                wM.invoke(sourceObj, name);
            }
        }
    }

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
     * 获取MappingDataBinding具体实现类的名称
     *
     * @param type IdMappingTarget中设置的type值
     * @return
     */
//    private static String getClassName(String type){
//
//        int splitNum = type.indexOf(SPLIT);
//        String prefix = type;
//        if(splitNum != -1){
//			prefix = type.substring(0, splitNum-1);
//		}
//
//        return  upperCase(prefix.toLowerCase()) + SUFFIX;
//    }

    /**
     * 将字符串首字母转大写
     * @param str
     * @return
     */
    private static String upperCase(String str) {
        if ((str == null) || (str.length() == 0)) {
			return str;
		}
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
