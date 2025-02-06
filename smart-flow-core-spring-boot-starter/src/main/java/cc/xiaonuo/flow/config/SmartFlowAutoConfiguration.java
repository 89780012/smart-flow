package cc.xiaonuo.flow.config;

import cc.xiaonuo.common.config.SmartFlowProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cc.xiaonuo.flow")
@EnableConfigurationProperties(SmartFlowProperties.class)
@ConditionalOnProperty(prefix = "smart.flow", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SmartFlowAutoConfiguration {

}