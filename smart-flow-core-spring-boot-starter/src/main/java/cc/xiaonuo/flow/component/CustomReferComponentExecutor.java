package cc.xiaonuo.flow.component;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.method.CommonUtils;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.model.PropertyParam;
import cc.xiaonuo.common.enums.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@FlowComponent("flow-custom-refer")
public class CustomReferComponentExecutor extends AbstractComponentExecutor{

    @Override
    public String getType() {
        return "flow-custom-refer";
    }
    
    @Override
    protected void doExecute(CusComponent cusComponent, FlowContext context) {
        // 没有该属性, 不执行
        if (cusComponent.getProperty() == null) {
            return ;
        }
        String beanRef = cusComponent.getProperty().getBeanRef();
        String methodName = cusComponent.getProperty().getMethod();
        List<PropertyParam> params = cusComponent.getProperty().getParams();

        if (!StringUtils.hasText(beanRef) || !StringUtils.hasText(methodName)) {
            // 抛出异常
            throw new FlowException("beanRef和methodName不能为空");
        }

        try {
            Object bean = super.applicationContext.getBean(beanRef);
            Method method = super.findMethod(bean.getClass(), methodName, params);
            if (method == null) {
                // 抛出异常
                throw new FlowException("找不到方法: " + beanRef + "." + methodName);
            }

            log.debug("准备调用方法: bean={}, method={}, paramTypes={}",
                    bean.getClass().getName(),
                    method.getName(),
                    method.getParameterTypes());

            Object[] objects = parseParams(params, context);

            method.invoke(bean,objects); //可以在context中调用setVariable 进行赋值
        } catch (Exception e) {
            log.error("执行方法失败: {} - {}, 具体错误: {}", beanRef, methodName, e.getMessage(), e);
            throw new FlowException("执行方法失败: " + beanRef + "-" + methodName + ", 原因: " + e.getMessage());
        }
    }

    public Object[] parseParams(List<PropertyParam> params, FlowContext context){
        List arr = new ArrayList();
        if(params != null){
            for(int i=0;i<params.size();i++){
                PropertyParam propertyParam = params.get(i);
                String val = propertyParam.getVal();
                DataType dataType = DataType.getByValue(Integer.parseInt(propertyParam.getDataType()));

                Object param = CommonUtils.getParam(val, context);
                Object o = convertConstantValue(param, dataType);
                arr.add(o);
            }
        }
        arr.add(context);
        return arr.toArray();
    }



} 