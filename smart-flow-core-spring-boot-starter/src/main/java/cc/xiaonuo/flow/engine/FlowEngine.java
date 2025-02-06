package cc.xiaonuo.flow.engine;

import java.util.concurrent.Future;

public interface FlowEngine {
    /**
     * 同步执行流程
     * @param flowId 流程ID
     * @param context 流程上下文
     * @return 执行结果
     */
    Object execute(String flowId, FlowContext context);

    /**
     * 异步执行流程
     * @param flowId 流程ID
     * @param context 流程上下文
     * @return Future对象
     */
    Future<Object> executeAsync(String flowId, FlowContext context);
}