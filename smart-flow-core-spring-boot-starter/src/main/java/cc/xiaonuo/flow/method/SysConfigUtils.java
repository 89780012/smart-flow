package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@FlowComponent("flow-sysConfigUtil")
public class SysConfigUtils extends CommonUtils {

    @Autowired
    private Environment environment;

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        // 获取系统配置参数名称
        PropertyParam sysConfigParam = params.stream()
                .filter(p -> p.getSeq().equals("1"))
                .findFirst()
                .orElse(null);
        
        if (sysConfigParam == null || StrUtil.isBlank(sysConfigParam.getVal())) {
            log.warn("系统配置参数名称为空");
            return;
        }

        // 获取绑定的目标变量名
        PropertyParam bindParam = params.stream()
                .filter(p -> p.getSeq().equals("2"))
                .findFirst()
                .orElse(null);
                
        if (bindParam == null || StrUtil.isBlank(bindParam.getVal())) {
            log.warn("绑定参数名称为空");
            return;
        }

        // 获取默认值（可选）
        String defaultValue = params.stream()
                .filter(p -> p.getSeq().equals("3"))
                .map(PropertyParam::getVal)
                .findFirst()
                .orElse(null);

        String configKey = sysConfigParam.getVal();
        String bindKey = bindParam.getVal();

        // 1. 先尝试从系统环境变量获取
        String value = System.getenv(configKey);
        
        // 2. 如果系统环境变量没有，则从application配置获取
        if (StrUtil.isBlank(value)) {
            value = environment.getProperty(configKey);
        }
        
        // 3. 如果还是没有，使用默认值
        if (StrUtil.isBlank(value)) {
            value = defaultValue;
        }

        // 将获取到的值设置到上下文中
        if (StrUtil.isNotBlank(value)) {
            log.debug("获取配置 {} = {}", configKey, value);
            context.setVariable(bindKey, value);
        } else {
            log.warn("未能获取配置值: {}", configKey);
            throw new FlowException("未能获取配置值: " + configKey);
        }
    }

}
