package cc.xiaonuo.flow.component;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.model.PropertyParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@FlowComponent("user-custom-refer")
public class UserCustomComponentExecutor extends AbstractComponentExecutor{

    @Override
    public String getType() {
        return "user-custom-refer";
    }
    
    @Override
    protected void doExecute(CusComponent cusComponent, FlowContext context) {

        String beanRef = cusComponent.getProperty().getBeanRef();
        String methodName = cusComponent.getProperty().getMethod();
        List<PropertyParam> params = cusComponent.getProperty().getParams();

        if (!StringUtils.hasText(beanRef) || !StringUtils.hasText(methodName)) {
            // 抛出异常
            throw new FlowException("beanRef和methodName不能为空");
        }

        try {
            Object bean = super.applicationContext.getBean(beanRef);
            Method method = findMethod(bean.getClass(), methodName);
            if (method == null) {
                // 抛出异常
                throw new FlowException("找不到方法: " + beanRef + "." + methodName);
            }

            log.debug("准备调用方法: bean={}, method={}",
                    bean.getClass().getName(),
                    method.getName());

            List<Map<String,String>> objects = parseParams(params);
            Object[] array = new Object[objects.size() + 1];

            int i = 0;
            for(i=0;i<objects.size();i++){
                Map<String,String> map = objects.get(i);
                array[i] = map;
            }
            array[i] = context;

            method.invoke(bean,array); //可以在context中调用setVariable 进行赋值
        } catch (Exception e) {
            log.error("执行方法失败: {} - {}, 具体错误: {}", beanRef, methodName, e.getCause().getMessage(), e);
            throw new FlowException(e.getCause().getMessage());
        }
    }

    public List<Map<String,String>> parseParams(List<PropertyParam> params){
        List<Map<String,String>> arr = new ArrayList();

        for(int i=0;i<params.size();i++){
            PropertyParam propertyParam = params.get(i);
            String seq = propertyParam.getSeq();
            String val = propertyParam.getVal();
            Map<String,String> map = new HashMap<>();
            map.put("seq",seq);
            map.put("val",val);
            arr.add(map);
        }
        return arr;
    }

    protected Method findMethod(Class<?> clazz, String methodName) {
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

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(actualMethodName)) {
                return method;
            }
        }
        return null;
    }

} 