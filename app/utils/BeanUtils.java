package utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.Logger;
import play.libs.Json;

public class BeanUtils {
    private static final Logger.ALogger LOGGER = Logger.of(BeanUtils.class);

    /**
     * 传入一个bean获取其属性值为空的属性集合
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 传入一个bean获取其属性值不为空的属性集合
     *
     * @param source
     * @return
     */
    public static String[] getNotNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                getNullPropertyNames(source));
    }

    public static void copyProperties(Object source, Object target,
                                      String[] ingore) {
        List<String> list = new LinkedList<>(Arrays.asList(ingore));
        list.addAll(Arrays.asList(getNullPropertyNames(source)));
        String[] all = new String[list.size()];
        list.toArray(all);
        org.springframework.beans.BeanUtils.copyProperties(source, target, all);
    }

    public static void setValue(Object object, String fieldName, Object newValue) {
        Field field;
        Object oldValue;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            oldValue = field.get(object);
            if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                field.set(object, newValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("set value error ", e);
        }
    }

    /**
     * 传入一个对象与要获取属性值的名称，返回该属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getPropertyValue(Object object, String fieldName) {
        final BeanWrapper src = new BeanWrapperImpl(object);
        Object value = src.getPropertyValue(fieldName);
        return value;
    }

    public static <T> List<T> castEntity(List<Object[]> list, Class<T> clazz) {
        List<T> returnList = new ArrayList<>();
        if (list.size() > 0) {
            Object[] co = list.get(0);
            Class[] c2 = new Class[co.length];

            for (int i = 0; i < co.length; i++) {
                c2[i] = co[i].getClass();
            }

            for (Object[] o : list) {
                Constructor<T> constructor;
                try {
                    constructor = clazz.getConstructor(c2);
                    returnList.add(constructor.newInstance(o));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    LOGGER.error("构建" + clazz.getName() + "失败", e);
                }
            }
        }
        return returnList;
    }

    /**
     * 将JsonNode转成指定的类
     *
     * @param jsonNode
     * @param valueType
     * @return
     */
    public static <T> T castEntityFromJsonNode(JsonNode jsonNode, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (T) mapper.readValue(Json.stringify(jsonNode), valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 匹配指定class中数据,并返回包含get和set方法的object
     *
     * @param clazz
     * @param beanProperty
     * @return
     * @author chenpeng
     */
    private static Object[] beanMatch(Class clazz, String beanProperty) {
        Object[] result = new Object[2];
        char beanPropertyChars[] = beanProperty.toCharArray();
        beanPropertyChars[0] = Character.toUpperCase(beanPropertyChars[0]);
        String s = new String(beanPropertyChars);
        String names[] = {("set" + s).intern(), ("get" + s).intern(),
                ("is" + s).intern(), ("write" + s).intern(),
                ("read" + s).intern()};
        Method getter = null;
        Method setter = null;
        Method methods[] = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            // 只取公共字段
            if (!Modifier.isPublic(method.getModifiers()))
                continue;
            String methodName = method.getName().intern();
            for (int j = 0; j < names.length; j++) {
                String name = names[j];
                if (!name.equals(methodName))
                    continue;
                if (methodName.startsWith("set")
                        || methodName.startsWith("read"))
                    setter = method;
                else
                    getter = method;
            }
        }
        result[0] = getter;
        result[1] = setter;
        return result;
    }

    /**
     * 为bean自动注入数据
     *
     * @param object
     * @param beanProperty
     * @author chenpeng
     */
    private static void beanRegister(Object object, String beanProperty, String value) {
        Object[] beanObject = beanMatch(object.getClass(), beanProperty);
        Object[] cache = new Object[1];
        Method getter = (Method) beanObject[0];
        Method setter = (Method) beanObject[1];
        try {
            // 通过get获得方法类型
            String methodType = getter.getReturnType().getName();
            if (methodType.equalsIgnoreCase("long")) {
                cache[0] = new Long(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("int")
                    || methodType.equalsIgnoreCase("integer")) {
                cache[0] = new Integer(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("short")) {
                cache[0] = new Short(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("float")) {
                cache[0] = new Float(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("double")) {
                cache[0] = new Double(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("boolean")) {
                cache[0] = new Boolean(value);
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("java.lang.String")) {
                cache[0] = value;
                setter.invoke(object, cache);
            } else if (methodType.equalsIgnoreCase("java.io.InputStream")) {
            } else if (methodType.equalsIgnoreCase("char")) {
                cache[0] = (Character.valueOf(value.charAt(0)));
                setter.invoke(object, cache);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T>List<T> castEntityListFromResultSet(final ResultSet rs, final Class clazz) {
        List<T> returnList = Lists.newArrayList();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获得数据列数
            int cols = rsmd.getColumnCount();
            // 遍历结果集
            while (rs.next()) {
                // 创建对象
                Object object = null;
                // 从class获得对象实体
                object = clazz.newInstance();
                // 循环每条记录
                for (int i = 1; i <= cols; i++) {
                    beanRegister(object, rsmd.getColumnName(i), rs.getString(i));
                }
                // 将数据插入collection
                returnList.add((T) object);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {

        }
        return returnList;
    }

    public static void main(String[] args) {

    }
}
