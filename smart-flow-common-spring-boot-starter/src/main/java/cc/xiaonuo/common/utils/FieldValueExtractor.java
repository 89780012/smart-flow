package cc.xiaonuo.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldValueExtractor {
    private static final Logger log = LoggerFactory.getLogger(FieldValueExtractor.class);

    public static void extractFields(Object fieldValue, Map<String, Object> rawResult) {
        // 参数校验
        if (fieldValue == null || rawResult == null) {
            return;
        }

        // 处理Map类型
        if (fieldValue instanceof Map) {
            Map<?, ?> mapValue = (Map<?, ?>) fieldValue;
            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                if (entry.getKey() != null) {
                    rawResult.put(entry.getKey().toString(), entry.getValue());
                }
            }
            return;
        }

        // 处理普通对象
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(fieldValue.getClass());
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String propertyName = propertyDescriptor.getName();
                // 跳过class属性
                if ("class".equals(propertyName)) {
                    continue;
                }

                if (propertyDescriptor.getReadMethod() != null) {
                    Object propertyValue = propertyDescriptor.getReadMethod().invoke(fieldValue);
                    rawResult.put(propertyName, propertyValue);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to extract field values from object: {}", fieldValue, e);
            throw new RuntimeException("Field extraction failed", e);
        }
    }

    // 提供一个便捷的方法
    public static Map<String, Object> extract(Object fieldValue) {
        Map<String, Object> result = new HashMap<>();
        extractFields(fieldValue, result);
        return result;
    }
}