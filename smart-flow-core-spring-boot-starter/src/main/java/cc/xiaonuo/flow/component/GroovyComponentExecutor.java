package cc.xiaonuo.flow.component;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.script.GroovyEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FlowComponent("flow-groovy")
public class GroovyComponentExecutor extends AbstractComponentExecutor {

    @Autowired
    GroovyEngineService scriptEngineService;

    @Override
    public String getType() {
        return "flow-groovy";
    }

    @Override
    protected void doExecute(CusComponent cusComponent, FlowContext context) {
        // 获取脚本内容
        String script = cusComponent.getProperty().getScript();
        if (script == null || script.trim().isEmpty()) {
            throw new FlowException("script语句不能为空");
        }

        script = removeCDATA(script);

        try {
            // 执行脚本并获取结果
            scriptEngineService.executeScript(script, context.getVariables());
        } catch (Exception e) {
            log.error("执行JS脚本异常", e);
            throw new FlowException("script异常: " + e.getMessage());
        }
    }

    private String removeCDATA(String sql) {
        sql = sql.trim();
        if (sql.startsWith("<![CDATA[") && sql.endsWith("]]>")) {
            sql = sql.substring(9, sql.length() - 3);
        }
        return sql;
    }



}