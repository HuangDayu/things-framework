package cn.huangdayu.things.engine.common;

import cn.hutool.core.map.multi.Table;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cn.huangdayu.things.engine.common.ThingsConstants.Methods.*;

/**
 * @author huangdayu
 */
@Slf4j
public class ThingsUtils {

    public static <R, C, V> void deleteTable(Table<R, C, V> table, Function<V, Boolean> function) {
        Set<Table.Cell<R, C, V>> collect = table.cellSet().parallelStream().filter(cell -> function.apply(cell.getValue())).collect(Collectors.toSet());
        for (Table.Cell<R, C, V> cell : collect) {
            table.remove(cell.getRowKey(), cell.getColumnKey());
        }
    }

    public static <K, V> void deleteMap(Map<K, V> map, Function<V, Boolean> function) {
        Set<Map.Entry<K, V>> collect = map.entrySet().parallelStream().filter(entry -> function.apply(entry.getValue())).collect(Collectors.toSet());
        for (Map.Entry<K, V> entry : collect) {
            map.remove(entry.getKey());
        }
    }


    public static <A extends Annotation> A findBeanAnnotation(Object bean, Class<A> annotationType) {
        Class<?> extensionClz = bean.getClass();
        if (AopUtils.isAopProxy(bean)) {
            extensionClz = ClassUtils.getUserClass(bean);
        }
        return AnnotationUtils.findAnnotation(extensionClz, annotationType);
    }


    /**
     * 泛型数据对象转换
     *
     * @param source
     * @param type
     * @param method
     * @param parameterIndex
     * @return
     */
    public static Object typeConvert(Object source, Class<?> type, Method method, int parameterIndex) {
        return JSON.parseObject(JSON.toJSONString(source), TypeReference.parametricType(type, getParameterTypes(method, parameterIndex)));
    }


    /**
     * 获取泛型类型
     *
     * @param method
     * @param parameterIndex
     * @param typeIndex
     * @return
     */
    public static Type getParameterType(Method method, int parameterIndex, int typeIndex) {
        MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
        return ((ParameterizedType) methodParameter.getGenericParameterType()).getActualTypeArguments()[typeIndex];
    }


    /**
     * 获取返回值类型列表
     *
     * @param method
     * @return
     */
    public static Type[] getReturnType(Method method) {
        return ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
    }

    public static Object returnTypeConvert(Object source, Class<?> type, Method method) {
        return JSON.parseObject(JSON.toJSONString(source), TypeReference.parametricType(type, getReturnType(method)));
    }

    /**
     * 获取泛型类型列表
     *
     * @param method
     * @param parameterIndex
     * @return
     */
    public static Type[] getParameterTypes(Method method, int parameterIndex) {
        MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
        return ((ParameterizedType) methodParameter.getGenericParameterType()).getActualTypeArguments();
    }


    /**
     * 复制注解字段值到目标对象
     *
     * @param annotation
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyAnnotationValues(Annotation annotation, T target) {
        Method[] methods = annotation.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getReturnType() != Void.TYPE) {
                    Object value = method.invoke(annotation);
                    Method setter = target.getClass().getMethod("set" + capitalize(method.getName()), method.getReturnType());
                    setter.invoke(target, value);
                }
            } catch (Exception ignore) {
            }
        }
        return target;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    @SafeVarargs
    public static <T> T findFirst(Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            try {
                T t = supplier.get();
                if (t instanceof Boolean) {
                    if ((Boolean) t) {
                        return t;
                    }
                    continue;
                }
                if (t != null) {
                    return t;
                }
            } catch (Exception e) {
                log.error("Find the first supplier exception {}", e.getMessage());
            }
        }
        return null;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static String subIdentifies(String method) {
        if (method.startsWith(SERVICE_START_WITH)) {
            return method.replace(SERVICE_START_WITH, "");
        }
        if (method.startsWith(PROPERTY_METHOD_START_WITH)) {
            return method.replace(PROPERTY_METHOD_START_WITH, "");
        }
        return method.substring(0, method.indexOf(".", 12)).replace(EVENT_LISTENER_START_WITH, "");
    }

}
