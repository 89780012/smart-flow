package cc.xiaonuo.flow.component;

import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.model.PropertyParam;
import cn.hutool.core.date.DateUtil;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.common.enums.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class AbstractComponentExecutor implements ComponentExecutor, ApplicationContextAware {

    public ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(CusComponent cusComponent, FlowContext context) {
        doExecute(cusComponent, context);
    }

    protected abstract void doExecute(CusComponent cusComponent, FlowContext context);

    // 通用bean引用函数处理
    public Object execBeanRef(CusComponent cusComponent, FlowContext context) {
        // 没有该属性, 不执行
        if (cusComponent.getProperty() == null) {
            return null;
        }
        String beanRef = cusComponent.getProperty().getBeanRef();
        String methodName = cusComponent.getProperty().getMethod();
        List<PropertyParam> params = cusComponent.getProperty().getParams();

        if (!StringUtils.hasText(beanRef) || !StringUtils.hasText(methodName)) {
            // 抛出异常
            throw new FlowException("beanRef和methodName不能为空");
        }

        try {
            Object bean = applicationContext.getBean(beanRef);
            Method method = findMethod(bean.getClass(), methodName, params);
            if (method == null) {
                // 抛出异常
                throw new FlowException("找不到方法: " + beanRef + "." + methodName);
            }

            log.debug("准备调用方法: bean={}, method={}, paramTypes={}",
                    bean.getClass().getName(),
                    method.getName(),
                    method.getParameterTypes());
            return method.invoke(bean, params, context);

        } catch (Exception e) {
            log.error("执行方法失败: {} - {}, 具体错误: {}", beanRef, methodName, e.getMessage(), e);
            throw new FlowException("执行方法失败: " + beanRef + "-" + methodName + ", 原因: " + e.getMessage());
        }
    }

    // 找到对应bean引用方法
    protected Method findMethod(Class<?> clazz, String methodName, List<PropertyParam> params) {
        // 解析类名和方法名
        String[] parts = methodName.split("#");
        if (parts.length != 2) {
            return null;
        }

        String className = parts[0];
        String actualMethodName = parts[1];

        // 验证类名是否匹配
        if (!clazz.getName().equals(className)) {
            return null;
        }

        // int paramCount = params != null ? params.size() : 0;
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(actualMethodName)) {
                return method;
            }
        }
        return null;
    }

    public Object convertConstantValue(Object value, DataType dataType) {
        try {
            switch (dataType) {
                case STRING:
                    return value;
                case INTEGER:
                    return Integer.parseInt(value.toString());
                case FLOAT:
                    return Float.parseFloat(value.toString());
                case DOUBLE:
                    return Double.parseDouble(value.toString());
                case BOOLEAN:
                    return Boolean.parseBoolean(value.toString());
                case DATE:
                    return parseDate(value.toString());
                case LONG:
                    return Long.parseLong(value.toString());
                case ARRAY:
                case OBJECT:
                    return value;
                case BigDecimal:
                    return new BigDecimal(value.toString());
                default:
                    return value;
            }
        } catch (Exception e) {
            return value;
        }
    }

    public Date parseDate(String DateStr) {
        return DateUtil.parse(DateStr);
    }
}