package cn.huangdayu.things.common.utils;

import cn.huangdayu.things.common.annotation.ThingsEventEntity;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.hutool.core.map.multi.Table;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
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
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.*;

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
        return findFirst(false, suppliers);
    }

    @SafeVarargs
    public static <T> T findFirst(boolean ignoreException, Function<T, Boolean> function, Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            try {
                T t = supplier.get();
                if (function.apply(t)) {
                    return t;
                }
            } catch (Exception e) {
                if (!ignoreException) {
                    log.error("Find the first supplier exception {}", e.getMessage());
                }
            }
        }
        return null;
    }

    @SafeVarargs
    public static <T> T findFirst(boolean ignoreException, Supplier<T>... suppliers) {
        return findFirst(ignoreException, t -> {
            if (t instanceof Boolean) {
                return (Boolean) t;
            }
            return t != null;
        }, suppliers);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static String subIdentifies(String method) {
        return method.split("\\.")[2];
    }


    public static JsonThingsMessage covertEventMessage(ThingsEventMessage tem) {
        ThingsEventEntity thingsEventEntity = findBeanAnnotation(tem, ThingsEventEntity.class);
        if (thingsEventEntity == null) {
            throw new ThingsException(BAD_REQUEST, "Message object is not ThingsEvent entry.");
        }
        JsonThingsMessage jtm = new JsonThingsMessage();
        jtm.setBaseMetadata(baseThingsMetadata -> {
            baseThingsMetadata.setProductCode(thingsEventEntity.productCode());
            baseThingsMetadata.setDeviceCode(tem.getDeviceCode());
        });
        jtm.setQos(thingsEventEntity.qos());
        jtm.setPayload((JSONObject) JSON.toJSON(tem, JSONWriter.Feature.WriteNulls));
        jtm.setMethod(THINGS_EVENT_POST.replace(THINGS_IDENTIFIER, thingsEventEntity.identifier()));
        return jtm;
    }

    /**
     * 根据给定的 Class 对象返回对应的数据类型别名。
     *
     * @param clazz 给定的 Class 对象。
     * @return 对应的数据类型别名。
     */
    public static String convertDataTypeName(Class<?> clazz) {
        switch (clazz.getName()) {
            case "int", "java.lang.Integer":
                return "int";
            case "java.lang.String":
                return "text";
            case "boolean", "java.lang.Boolean":
                return "bool";
            case "java.util.Date":
                return "date";
            case "float", "java.lang.Float":
                return "float";
            case "double", "java.lang.Double":
                return "double";
            case "long", "java.lang.Long":
                return "long";
            default:
                if (clazz.isEnum()) {
                    return "enum";
                } else if (clazz.isArray()) {
                    return "array";
                } else {
                    return "struct";
                }
        }
    }

    /**
     * 将字符串类型的名称转换为对应的 Class 对象。
     *
     * @param typeName 类型名称的字符串表示形式。
     * @return 对应的 Class 对象。
     */
    public static Class<?> convertToClass(String typeName) {
        return switch (typeName) {
            case "int" -> Integer.class;
            case "text" -> String.class;
            case "bool" -> Boolean.class;
            case "date" -> Date.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            case "long" -> Long.class;
            case "enum" -> Enum.class;
            case "array" -> Object[].class;
            case "struct" -> Object.class;
            default -> throw new IllegalArgumentException("Unsupported type name: " + typeName);
        };
    }

    public static boolean isServiceRequest(JsonThingsMessage jtm) {
        return THINGS_SERVICE_REQUEST.equals(jtm.getMethod().replace(subIdentifies(jtm.getMethod()), THINGS_IDENTIFIER));
    }

    public static boolean isPropertiesSetOrGet(JsonThingsMessage jtm) {
        return THINGS_PROPERTIES_SET.equals(jtm.getMethod()) || THINGS_PROPERTIES_GET.equals(jtm.getMethod());
    }

    public static boolean isEventPost(JsonThingsMessage jtm) {
        return THINGS_EVENT_POST.equals(jtm.getMethod().replace(subIdentifies(jtm.getMethod()), THINGS_IDENTIFIER));
    }
}
