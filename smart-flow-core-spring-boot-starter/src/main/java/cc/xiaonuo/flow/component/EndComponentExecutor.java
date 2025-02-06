package cc.xiaonuo.flow.component;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;

@FlowComponent("flow-end")
public class EndComponentExecutor implements ComponentExecutor {
    @Override
    public String getType() {
        return "flow-end";
    }
    
    @Override
    public void execute(CusComponent cusComponent, FlowContext context) {
    }
} 