package cc.xiaonuo.flow.log;

import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.Connection;
import cc.xiaonuo.flow.model.CusComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Component
public class FlowLogger {
    private static final Logger logger = LoggerFactory.getLogger(FlowLogger.class);
    private static final String PATH_SEPARATOR = " → ";
    private static final String LOG_PREFIX = "【Flow Engine】";
    
    private ThreadLocal<List<String>> currentPath = new ThreadLocal<>();
    private ThreadLocal<Set<String>> visitedComponents = new ThreadLocal<>();

    public void logFlowStart(String flowId,String name, FlowContext context) {
        currentPath.set(new ArrayList<>());
        visitedComponents.set(new HashSet<>());
        //String flowName = "Flow Start: " + flowId + " - " + name;
        //logger.info("{}", formatSeparator(flowName));
        logger.info("{} Params: {}", LOG_PREFIX, context.getParams());
    }
    
    public void logFlowEnd(String flowId,String name, Object result) {
        List<String> path = currentPath.get();
        String executionPath = String.join(PATH_SEPARATOR, path);
        //String flowName = "Flow End: " + flowId + " - " + name;
        
        logger.info("{} Path: {}", LOG_PREFIX, executionPath);
        logger.info("{} Result: {}", LOG_PREFIX, result);
        //logger.info("{}", formatSeparator(flowName));
        currentPath.remove();
        visitedComponents.remove();
    }
    
    public void logFlowError(String flowId, Exception e) {
        List<String> path = currentPath.get();
        String executionPath = String.join(PATH_SEPARATOR, path);
        
        logger.error("{} Error Path: {}", LOG_PREFIX, executionPath);
        logger.error("{} Error Message: {}", LOG_PREFIX, e.getMessage());
        logger.error("Exception Stack:", e);
        currentPath.remove();
        visitedComponents.remove();
    }
    
    public void logComponentStart(CusComponent component) {
        List<String> path = currentPath.get();
        Set<String> visited = visitedComponents.get();
        if (path != null && visited != null && !visited.contains(component.getId())) {
            path.add(formatComponent(component));
            visited.add(component.getId());
        }
        logger.debug("{} Execute Component: {}", LOG_PREFIX, formatComponent(component));
    }

    public void logConnection(Connection connection, CusComponent from, CusComponent to) {
        List<String> path = currentPath.get();
        Set<String> visited = visitedComponents.get();
        if (path != null && visited != null) {
            if (StringUtils.hasText(connection.getLabel())) {
                path.add(String.format("【%s】", connection.getLabel()));
            }
            if (!visited.contains(to.getId())) {
                path.add(formatComponent(to));
                visited.add(to.getId());
            }
        }
        logger.debug("{} Connection: {} -> {}", LOG_PREFIX, formatComponent(from), formatComponent(to));
        if (StringUtils.hasText(connection.getLabel())) {
            logger.debug("{} Condition: {}", LOG_PREFIX, connection.getLabel());
        }
    }
    
    private String formatComponent(CusComponent component) {
        return String.format("%s(%s)", component.getName(), component.getType());
    }
} 