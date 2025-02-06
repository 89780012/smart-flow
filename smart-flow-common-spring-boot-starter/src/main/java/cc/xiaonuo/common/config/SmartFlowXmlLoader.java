package cc.xiaonuo.common.config;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@Slf4j
public class SmartFlowXmlLoader {
    
    private final ResourceLoader resourceLoader;
    private final SmartFlowProperties properties;

    public SmartFlowXmlLoader(ResourceLoader resourceLoader, SmartFlowProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }
    
    public SmartFlowConfig loadXmlConfig() {
        try {
            String location = properties.getConfigLocation();
            log.info("尝试加载配置文件，位置: {}", location);

            if (!StringUtils.hasText(location)) {
                log.warn("未指定flow.xml位置，请检查配置");
                return null;
            }

            Resource resource = resourceLoader.getResource(location);
            log.info("配置文件资源: {}, 是否存在: {}", resource.getURI(), resource.exists());

            if (!resource.exists()) {
                log.warn("未找到flow.xml，位置: {}", location);
                return null;
            }

            return SmartFlowXmlParser.parse(resource.getInputStream());
        } catch (Exception e) {
            log.error("Failed to load flow.xml", e);
            throw new RuntimeException("Failed to load flow.xml", e);
        }
    }
} 