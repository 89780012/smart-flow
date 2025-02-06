package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.cache.PluginCache;
import cc.xiaonuo.common.enums.BasicDataType;
import cc.xiaonuo.common.enums.UniqueIdType;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import cc.xiaonuo.flow.utils.SnowFlakeIdWorker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@FlowComponent("flow-type2typeUtil")
public class Type2typeUtils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        //获取输入字符串
        PropertyParam inputParam = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        String inputParamStr = inputParam.getVal();

        PropertyParam targetType = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        String targetTypeStr = targetType.getVal();
        BasicDataType basicDataType = BasicDataType.valueOf(targetTypeStr);

        //获取绑定字符串
        PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
        String bindParamValue = bindParam.getVal();

        // 从context获取输入值
        Object inputValue = CommonUtils.getParam(inputParamStr,context);
        if (inputValue == null) {
            return;
        }

        // 进行类型转换
        Object result = null;
        try {
            switch (basicDataType) {
                case STRING:
                    result = String.valueOf(inputValue);
                    break;
                case INTEGER:
                    if (inputValue instanceof String) {
                        result = Integer.parseInt((String) inputValue);
                    } else if (inputValue instanceof Number) {
                        result = ((Number) inputValue).intValue();
                    }
                    break;
                case LONG:
                    if (inputValue instanceof String) {
                        result = Long.parseLong((String) inputValue);
                    } else if (inputValue instanceof Number) {
                        result = ((Number) inputValue).longValue();
                    }
                    break;
                case DOUBLE:
                    if (inputValue instanceof String) {
                        result = Double.parseDouble((String) inputValue);
                    } else if (inputValue instanceof Number) {
                        result = ((Number) inputValue).doubleValue();
                    }
                    break;
                case BOOLEAN:
                    if (inputValue instanceof String) {
                        result = Boolean.parseBoolean((String) inputValue);
                    } else {
                        result = Boolean.valueOf(String.valueOf(inputValue));
                    }
                    break;
                case BIGDECIMAL:
                    if (inputValue instanceof String) {
                        result = new BigDecimal((String) inputValue);
                    } else if (inputValue instanceof Number) {
                        result = BigDecimal.valueOf(((Number) inputValue).doubleValue());
                    }
                    break;
                default:
                    result = inputValue;
            }
        } catch (Exception e) {
            // 转换失败时保持原值
            result = inputValue;
        }

        // 将结果存入context
        if (result != null) {
            context.setVariable(bindParamValue, result);
        }
    }
}
