package cc.xiaonuo.flow.script;

import cc.xiaonuo.common.exception.FlowException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GroovyEngineService {
    
    private GroovyShell groovyShell;
    // 用于缓存编译后的脚本
    private final ConcurrentHashMap<String, Script> scriptCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        // 初始化GroovyShell
        groovyShell = new GroovyShell(config);
        log.info("Groovy引擎初始化完成");
    }
    
    public boolean evaluateCondition(String expression, Map<String, Object> context) {
        if (expression == null || expression.trim().isEmpty()) {
            return true;
        }

        try {
            Binding binding = new Binding();
            // 处理上下文变量
            context.forEach((key, value) -> binding.setVariable(key, value));

            // 获取或编译脚本
            Script script = scriptCache.computeIfAbsent(expression,
                key -> groovyShell.parse(expression));
            script.setBinding(binding);
            Object result = script.run();
            return result instanceof Boolean ? (Boolean) result : false;
        } catch (Exception e) {
            log.error("条件表达式执行异常: expression={}, error={}", expression, e.getMessage());
            throw new RuntimeException("条件表达式执行异常: " + e.getMessage(), e);
        }
    }

    public Object executeScript(String script, Map<String,Object> vars) {
        try {
            Binding binding = new Binding();
            binding.setVariable("$vars", vars);
            
            // 获取或编译脚本
            Script groovyScript = scriptCache.computeIfAbsent(script,
                key -> groovyShell.parse(script));
                
            groovyScript.setBinding(binding);
            return groovyScript.run();
        } catch (Exception e) {
            throw new FlowException("执行脚本失败: " + e.getMessage());
        }
    }
    
    // 清理缓存的方法
    public void clearCache() {
        scriptCache.clear();
    }
}