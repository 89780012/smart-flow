package cc.xiaonuo.flow.component;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;

@FlowComponent("flow-start")
public class StartComponentExecutor implements ComponentExecutor {
    @Override
    public String getType() {
        return "flow-start";
    }
    
    @Override
    public void execute(CusComponent cusComponent, FlowContext context) {
        // 开始节点可以进行参数校验等操作
        return ;
    }
} 