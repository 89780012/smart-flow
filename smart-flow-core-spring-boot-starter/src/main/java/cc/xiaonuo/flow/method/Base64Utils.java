package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.enums.Base64OperationType;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;

import java.util.List;

@FlowComponent("flow-base64Util")
public class Base64Utils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        //获取操作类型
        PropertyParam operatorTypeParam = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        Base64OperationType base64OperationType = Base64OperationType.valueOf(operatorTypeParam.getVal());

        //获取入参
        PropertyParam inParam = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        String inParamStr = inParam.getVal();
        Object obj = CommonUtils.getParam(inParamStr, context);
        if(obj == null){
            throw new FlowException(inParamStr + "参数为空,无法进行base64操作");
        }
        if(obj instanceof String){

            String inParamValue = (String)obj;

            //获取绑定值
            PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
            String bindParamValue = bindParam.getVal();

            switch (base64OperationType) {
                case ENCODE:
                    context.setVariable(bindParamValue, java.util.Base64.getEncoder().encodeToString(inParamValue.getBytes()));
                    break;
                case DECODE:
                    context.setVariable(bindParamValue, new String(java.util.Base64.getDecoder().decode(inParamValue)));
                    break;
            }
        }else{
            throw new FlowException(inParamStr + "参数不是字符串类型,无法进行base64操作");
        }
    }
}
