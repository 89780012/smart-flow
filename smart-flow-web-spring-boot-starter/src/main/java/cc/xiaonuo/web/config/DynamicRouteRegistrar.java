package cc.xiaonuo.web.config;

import cc.xiaonuo.flow.engine.FlowEngine;
import cc.xiaonuo.flow.model.BizDefinition;
import cc.xiaonuo.flow.service.FlowLoaderService;
import cc.xiaonuo.web.controller.DynamicFlowController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
public class DynamicRouteRegistrar {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private FlowLoaderService flowLoaderService;

    @Autowired
    private FlowEngine flowEngine;

    // 添加以下静态字段用于存储实例引用
    private static DynamicRouteRegistrar instance;

    @PostConstruct
    public void init() {
        instance = this;
        registerRoutes();
    }

    public void registerRoutes() {
        Map<String, BizDefinition> flows = flowLoaderService.getAllFlows();
        flows.values().forEach(this::registerFlow);
    }

    private void registerFlow(BizDefinition flow) {
        try {
            String url = flow.getUrl().startsWith("/") ? flow.getUrl() : "/" + flow.getUrl();
            
            RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(url)
                .methods(RequestMethod.valueOf(flow.getMethod()));

            RequestMappingInfo mappingInfo = builder
                .options(new RequestMappingInfo.BuilderConfiguration())
                .build();

            DynamicFlowController controller = new DynamicFlowController(flowEngine, flow.getId());

            Method executeMethod = DynamicFlowController.class.getDeclaredMethod("executeFlow", HttpServletRequest.class, HttpServletResponse.class);
            
            handlerMapping.registerMapping(mappingInfo, controller, executeMethod);

        } catch (Exception e) {
            log.error("注册流程路由失败: {} {}", flow.getId(), e.getMessage(), e);
        }
    }

}