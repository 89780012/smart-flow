package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.enums.DataType;
import cc.xiaonuo.common.enums.ValueCategory;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;

import java.util.List;

/**
 * 将流程左边变量赋值给右边变量
 */
@FlowComponent("flow-assignUtil")
public class AssignUtils extends CommonMethod{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        try {
            for (PropertyParam param : params) {
                String leftKey = param.getVal();
                String rightKey = param.getVal2();
                DataType dataType = DataType.getByValue(Integer.parseInt(param.getDataType()));
                ValueCategory valueCategory = ValueCategory.getByValue(Integer.parseInt(param.getValueCategory()));

                Object newValue = null;

                // 根据数据类型和值类型进行赋值
                switch (valueCategory) {
                    case CONSTANT:
                        newValue = convertConstantValue(rightKey, dataType);
                        context.setVariable(leftKey, newValue);
                        break;
                    case VARIABLE:
                        newValue = CommonUtils.getParam(rightKey, context);
                        context.setVariable(leftKey, newValue);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
