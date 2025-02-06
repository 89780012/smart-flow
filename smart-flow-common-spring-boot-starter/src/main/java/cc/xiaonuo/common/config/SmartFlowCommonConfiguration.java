package cc.xiaonuo.common.config;

import cc.xiaonuo.common.bean.SmartFlowConfig;

import cc.xiaonuo.common.cache.PluginCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@ComponentScan("cc.xiaonuo.common")
@EnableConfigurationProperties(SmartFlowProperties.class)
@ConditionalOnProperty(prefix = "smart.flow", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SmartFlowCommonConfiguration {
    @Bean
    public SmartFlowXmlLoader smartFlowXmlLoader(ResourceLoader resourceLoader,
                                                 SmartFlowProperties properties) {
        return new SmartFlowXmlLoader(resourceLoader, properties);
    }

    @Bean
    public SmartFlowConfig smartFlowConfig(SmartFlowXmlLoader xmlLoader) {
        SmartFlowConfig config = xmlLoader.loadXmlConfig();
        // 读取XML配置并初始化数据源
        PluginCache.unique_roomid = Integer.parseInt(config.getSettings().getProperty("unique_roomid"));
        PluginCache.unique_workid = Integer.parseInt(config.getSettings().getProperty("unique_workid"));
        return config;
    }
}
