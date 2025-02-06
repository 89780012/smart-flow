package cc.xiaonuo.flow.component;

import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;

public interface ComponentExecutor {

    String getType();
    
    void execute(CusComponent cusComponent, FlowContext context);
} 