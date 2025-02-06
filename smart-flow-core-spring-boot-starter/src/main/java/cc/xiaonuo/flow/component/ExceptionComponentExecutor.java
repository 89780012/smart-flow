package cc.xiaonuo.flow.component;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;

@FlowComponent("flow-exception")
public class ExceptionComponentExecutor implements ComponentExecutor {
    @Override
    public String getType() {
        return "flow-exception";
    }
    
    @Override
    public void execute(CusComponent cusComponent, FlowContext context) {
        // 获取异常信息
        String exception = cusComponent.getProperty().getParams().get(0).getVal();
        throw new FlowException(exception);
    }
} 