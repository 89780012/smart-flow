package cc.xiaonuo.common.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smart.flow")
public class SmartFlowProperties {

    private boolean enabled = true;

    private String configLocation = "classpath:flow.xml";
}