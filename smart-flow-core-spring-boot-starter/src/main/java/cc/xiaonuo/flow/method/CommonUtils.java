package cc.xiaonuo.flow.method;

import cc.xiaonuo.flow.engine.FlowContext;

public class CommonUtils {

    public static Object getParam(String key, FlowContext context){
        Object variable = context.getVariable(key);
        if (variable != null){
            return context.getVariable(key);
        }
        Object param = context.getParam(key);
        if (param != null){
            return context.getParam(key);
        }
        return null;
    }
}
