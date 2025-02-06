package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.enums.NumberOperationType;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@FlowComponent("flow-numberUtil")
public class NumberlUtils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        //获取第一个数
        PropertyParam num1 = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        String num1Value = num1.getVal();
        Object param = CommonUtils.getParam(num1Value, context);
        if(param == null){
            throw new FlowException( num1Value + "参数不存在, 请检查");
        }
        BigDecimal paramDecimal = new BigDecimal(param.toString());

        //获取运算符
        PropertyParam operator = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        String operatorValue = operator.getVal();
        NumberOperationType operationType = NumberOperationType.valueOf(operatorValue);

        //获取第二个数
        PropertyParam num2 = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
        String num2Value = num2.getVal();
        Object param2 = CommonUtils.getParam(num2Value, context);
        if(param2 == null){
            throw new FlowException( num2Value + "参数不存在, 请检查");
        }
        BigDecimal param2Decimal = new BigDecimal(param2.toString());

        //获取绑定值
        PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("4")).findFirst().orElse(null);
        String bindParamValue = bindParam.getVal();

        //根据运算符进行计算
        switch (operationType) {
            case ADD:
                context.setVariable(bindParamValue, paramDecimal.add(param2Decimal));
                break;
            case SUBTRACT:
                context.setVariable(bindParamValue, paramDecimal.subtract(param2Decimal));
                break;
            case MULTIPLY:
                context.setVariable(bindParamValue, paramDecimal.multiply(param2Decimal));
                break;
            case DIVIDE:
                context.setVariable(bindParamValue, paramDecimal.divide(param2Decimal));
                break;
        }
    }

}
