package cc.xiaonuo.flow.service;

import cc.xiaonuo.flow.model.BizDefinition;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

@Service("flowLoaderService")
@Slf4j
//@ManagedResource(objectName = "com.smart:type=FlowLoaderService,name=flowLoaderService")
public class FlowLoaderService{
    private final Map<String, BizDefinition> flowCache = new ConcurrentHashMap<>();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final Map<String, String> flowPathMapping = new ConcurrentHashMap<>();

    private String[] scanLocations;

    @PostConstruct
    public void init(){
        loadFlows();
    }


    public void loadFlows() {
        String[] scanLocations = new String[]{"classpath*:**/*.biz"};
        log.info("开始扫描流程文件, 配置的扫描路径: {}", Arrays.toString(scanLocations));
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        // 用于收集重复ID的信息
        StringBuilder duplicateErrors = new StringBuilder();

        for (String location : scanLocations) {
            try {
                Resource[] resources = resolver.getResources(location);
                log.info("在路径[{}]下找到{}个流程文件", location, resources.length);

                for (Resource resource : resources) {
                    try {
                        String path = resource.getURI().toString();
                        log.info("正在加载流程文件: {}", path);

                        BizDefinition biz = xmlMapper.readValue(resource.getInputStream(), BizDefinition.class);
                        
                        // 检查ID是否重复
                        if (flowCache.containsKey(biz.getId())) {
                            String existingPath = flowPathMapping.get(biz.getId());
                            String error = String.format(
                                "发现重复的流程ID: %s\n已存在文件路径: %s\n重复文件路径: %s\n\n",
                                biz.getId(), existingPath, path);
                            duplicateErrors.append(error);
                            continue; // 继续检查其他文件
                        }
                        
                        flowCache.put(biz.getId(), biz);
                        flowPathMapping.put(biz.getId(), path);
                        log.info("成功加载流程文件: {}, ID: {}", resource.getFilename(), biz.getId());
                        
                    } catch (Exception e) {
                        log.error("加载流程文件失败: {}", resource.getFilename(), e);
                    }
                }
            } catch (Exception e) {
                log.error("扫描路径失败: {}", location, e);
            }
        }

        // 如果存在重复ID,抛出异常阻止应用启动
        if (duplicateErrors.length() > 0) {
            String errorMessage = "流程定义加载失败,发现重复的流程ID:\n" + duplicateErrors.toString();
            throw new RuntimeException(errorMessage);
        }
        
        log.info("流程文件扫描完成，共加载{}个流程", flowCache.size());
    }

    public BizDefinition getFlow(String flowId) {
        return flowCache.get(flowId);
    }

    public void addFlow(String flowId, BizDefinition bizDefinition){
        flowCache.put(flowId,bizDefinition);
    }

    public Map<String, BizDefinition> getAllFlows() {
        return new ConcurrentHashMap<>(flowCache);
    }

    @ManagedOperation(description = "重新加载单个流程")
    public boolean loadSingleFlow(String flowId) {
        String flowPath = flowPathMapping.get(flowId);
        if (flowPath == null) {
            log.error("未找到流程定义文件: flowId={}", flowId);
            return false;
        }

        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(flowPath);
            
            if (!resource.exists()) {
                log.error("流程文件不存在: {}", flowPath);
                return false;
            }

            BizDefinition biz = xmlMapper.readValue(resource.getInputStream(), BizDefinition.class);
            flowCache.put(biz.getId(), biz);
            log.info("重新加载流程文件成功: flowId={}, path={}", flowId, flowPath);
            return true;
        } catch (Exception e) {
            log.error("重新加载流程文件失败: flowId={}, path={}", flowId, flowPath, e);
            return false;
        }
    }
}