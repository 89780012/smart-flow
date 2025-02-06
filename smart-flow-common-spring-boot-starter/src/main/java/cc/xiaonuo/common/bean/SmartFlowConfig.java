package cc.xiaonuo.common.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
public class SmartFlowConfig {
    private Map<String, Properties> dataSources = new HashMap<>();
    private Properties settings = new Properties();
    private Map<String, Properties> sftp = new HashMap<>();
    private Map<String, Properties> redis = new HashMap<>();
    private Properties mail = new Properties();
}