package cc.xiaonuo.flow.engine;

import cc.xiaonuo.common.bean.Result;
import cc.xiaonuo.common.enums.StepType;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.common.trans.FlowTransactionManager;
import cc.xiaonuo.common.utils.FieldValueExtractor;
import cc.xiaonuo.flow.model.*;
import cc.xiaonuo.flow.component.ComponentExecutor;
import cc.xiaonuo.flow.convert.FlowParamConvert;
import cc.xiaonuo.common.enums.ResponseStructType;
import cc.xiaonuo.common.enums.ThreadType;
import cc.xiaonuo.flow.script.GroovyEngineService;
import cc.xiaonuo.flow.service.FlowLoaderService;
import cc.xiaonuo.flow.log.FlowLogger;
import cc.xiaonuo.flow.valid.FlowParamValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class FlowEngineImpl implements FlowEngine {
    private static final String LOG_PREFIX = "【Flow Engine】";
    
    private final Map<String, ComponentExecutor> executors;
    private final FlowLoaderService flowLoaderService;
    private final FlowLogger flowLogger;
    private final GroovyEngineService scriptEngineService;  // 注入脚本引擎服务
    private final FlowParamValidator paramValidator;
    private final FlowParamConvert flowParamConvert;  // 注入参数转换服务

    @Autowired(required = false)
    private List<FlowTransactionManager> flowTransactionManagers;


    // 添加格式化分隔符方法
    private String formatSeparator(String flowName) {
        int stars = (256 - flowName.length()) / 2;
        StringBuilder starsBuilder = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            starsBuilder.append("*");
        }
        return String.format("%s %s %s", starsBuilder.toString(), flowName, starsBuilder.toString());
    }

    public FlowEngineImpl(
            Map<String, ComponentExecutor> executors, 
            FlowLoaderService flowLoaderService,
            FlowLogger flowLogger,
            GroovyEngineService scriptEngineService,
            FlowParamValidator paramValidator,
            FlowParamConvert flowParamConvert
           ) {
        this.executors = executors;
        this.flowLoaderService = flowLoaderService;
        this.flowLogger = flowLogger;
        this.scriptEngineService = scriptEngineService;
        this.paramValidator = paramValidator;
        this.flowParamConvert = flowParamConvert;
    }

    @PostConstruct
    public void init() {
        // 打印所有注册的执行器
        log.info("注册的组件执行器列表:");
        executors.forEach((type, executor) -> {
            log.info("类型: {}, 执行器: {}", type, executor.getClass().getSimpleName());
        });
    }
    
    @Override
    public Object execute(String flowId, FlowContext context) {
        String flowName = "Flow Execute start: " + flowId;
        String flowName2 = "Flow Execute end: " + flowId;
        try {
            log.info("{}", formatSeparator(flowName));
            log.info("{} 开始执行流程, flowId:{}", LOG_PREFIX, flowId);

            //放到variables 变量中
            context.getVariables().putAll(context.getParams());

            Object result = doExecute(flowId, context);
            // 提交事务
            flowTransactionManagers.get(0).commit();
            log.info("{} 流程执行完成, flowId:{}", LOG_PREFIX, flowId);
            log.info("{}", formatSeparator(flowName2));
            return result;
        } catch (Exception e) {
            // 发生异常时回滚事务
            flowTransactionManagers.get(0).rollback();
            log.error("{} 流程执行异常,将回滚事务, flowId:{}", LOG_PREFIX, flowId);
            log.error("{} 异常信息: {}", LOG_PREFIX, e.getMessage());
            log.info("{}", formatSeparator(flowName2));
            throw new FlowException(e.getMessage());
        } finally {
            flowTransactionManagers.get(0).clear(flowId);
        }
    }
    
    @Async
    @Override
    public Future<Object> executeAsync(String flowId, FlowContext context) {
        try {
            String flowName = "Flow Async Execute: " + flowId;
            log.info("{}", formatSeparator(flowName));
            log.info("{} 开始异步执行流程, flowId:{}", LOG_PREFIX, flowId);
            Object result = doExecute(flowId, context);
            // 提交事务
            flowTransactionManagers.get(0).commit();
            log.info("{} 异步流程执行完成, flowId:{}", LOG_PREFIX, flowId);
            log.info("{}", formatSeparator(flowName));
            return new AsyncResult<>(result);
        } catch (Exception e) {
            // 发生异常时回滚事务
            flowTransactionManagers.get(0).rollback();
            log.error("{} 异步流程执行异常,将回滚事务, flowId:{}", LOG_PREFIX, flowId);
            log.error("{} 异常信息: {}", LOG_PREFIX, e.getMessage());
            throw new FlowException("异步流程执行异常: " + e.getMessage());
        } finally {
            flowTransactionManagers.get(0).clear(flowId);
        }
    }
    
    private Object doExecute(String flowId, FlowContext context) {
        // 获取流程定义
        BizDefinition biz = flowLoaderService.getFlow(flowId);
        if (biz == null) {
            throw new FlowException("流程不存在: " + flowId);
        }

        context.setBizDefinition(biz);
        // 执行前对开始参数进行类型、必要性验证
        paramValidator.validate(biz, context);
        // 参数转换
        flowParamConvert.convertDataType(biz, context);

        // 记录流程开始
        flowLogger.logFlowStart(flowId, biz.getName(), context);
        
        Object result = null;
        try {
            // 构建组件执行图
            Map<String, CusComponent> componentMap = biz.getFlows().getCusComponent().stream()
                .collect(Collectors.toMap(CusComponent::getId, c -> c));
                
            // 找到开始节点
            CusComponent startCusComponent = biz.getFlows().getCusComponent().stream()
                .filter(c -> "flow-start".equals(c.getType()))
                .findFirst()
                .orElseThrow(() -> new FlowException("未找到开始节点"));
                
            // 构建连接关系
            Map<String, List<Connection>> connectionMap = biz.getFlows().getConnection().stream()
                .collect(Collectors.groupingBy(Connection::getFrom));
                
            // 执行流程
            try {
                executeComponent(startCusComponent, componentMap, connectionMap, context);
            } catch (FlowException e) {
                throw e;
            } catch (Exception e) {
                throw new FlowException(500,"流程执行异常: " + e.getMessage());
            }        

            // 构造返回结果
            result = constructResponse(context, biz.getResults());

            // 记录流程正常结束
            flowLogger.logFlowEnd(flowId, biz.getName(), result);
            return result;
            
        } catch (Exception e) {
            // 记录流程异常
            flowLogger.logFlowError(flowId, e);
            throw e;
        }
    }
    
    private void executeComponent(
            CusComponent current,
            Map<String, CusComponent> componentMap,
            Map<String, List<Connection>> connectionMap,
            FlowContext context) {
        
        flowLogger.logComponentStart(current);

        // 异常组件处理
        if ("flow-exception".equals(current.getType())) {
            String exception = current.getProperty().getParams().get(0).getVal();
            log.error("{} 执行异常组件[{}], 异常信息: {}", LOG_PREFIX, current.getName(), exception);
            throw new FlowException(exception);
        }

        ComponentExecutor executor = null;
        //自定义组件类型
        if(current.getType().startsWith("custom-")){
            executor = executors.get("user-custom-refer");
        }else{
            executor = executors.get(current.getType());
        }

        if (executor == null) {
            log.error("{} 找不到组件执行器: {}", LOG_PREFIX, current.getType());
            throw new FlowException("找不到组件:" + current.getType());
        }

        // 执行组件
        executeComponentWithThreadType(current, executor, context);

        // 获取下一个组件
        List<Connection> nextConnections = connectionMap.get(current.getId());
        if (nextConnections != null) {
            if (nextConnections.size() > 1) {
                // 多条连接线时进行条件判断
                handleMultipleConnections(current, nextConnections, componentMap, connectionMap, context);
            } else {
                // 单条连接线直接执行
                for (Connection conn : nextConnections) {
                    CusComponent nextComponent = componentMap.get(conn.getTo());
                    if (nextComponent != null) {
                        flowLogger.logConnection(conn, current, nextComponent);
                        executeComponent(nextComponent, componentMap, connectionMap, context);
                    }
                }
            }
        }
    }

    private void executeComponentWithThreadType(CusComponent component, ComponentExecutor executor, FlowContext context) {
        if (component.getProperty() != null && component.getProperty().getThreadType() != null &&
                ThreadType.ASYNC.getValue() == Integer.parseInt(component.getProperty().getThreadType())) {
            // 异步执行
            CompletableFuture.runAsync(() -> {
                try {
                    executor.execute(component, context);
                } catch (Exception e) {
                    log.error("异步执行组件失败: {}", e.getMessage(), e);
                    throw new FlowException("异步执行组件失败:" + e.getMessage());
                }
            });
        } else {
            // 同步执行
            executor.execute(component, context);
        }
    }

    private void handleMultipleConnections(
            CusComponent current,
            List<Connection> connections,
            Map<String, CusComponent> componentMap,
            Map<String, List<Connection>> connectionMap,
            FlowContext context) {
        
        boolean foundValidPath = false;
        Connection defaultConnection = null;

        // 遍历所有连接
        for (Connection conn : connections) {
            if (conn.getExpression() == null || conn.getExpression().trim().isEmpty()) {
                defaultConnection = conn;
                continue;
            }
            
            if (evaluateCondition(conn, context)) {
                CusComponent nextComponent = componentMap.get(conn.getTo());
                if (nextComponent != null) {
                    foundValidPath = true;
                    String label = String.format("[条件:%s]", conn.getExpression());
                    conn.setLabel(label);
                    flowLogger.logConnection(conn, current, nextComponent);
                    executeComponent(nextComponent, componentMap, connectionMap, context);
                    break;
                }
            }
        }

        // 如果没有满足条件的路径,使用默认路径
        if (!foundValidPath && defaultConnection != null) {
            CusComponent defaultComponent = componentMap.get(defaultConnection.getTo());
            if (defaultComponent != null) {
                defaultConnection.setLabel("[默认路径]");
                flowLogger.logConnection(defaultConnection, current, defaultComponent);
                executeComponent(defaultComponent, componentMap, connectionMap, context);
                foundValidPath = true;
            }
        }

        // 如果是排他网关且没有找到有效路径,抛出异常
        if (!foundValidPath && "paita".equals(current.getType())) {
            log.warn("排他网关[{}]没有找到满足条件的分支且无默认路径", current.getName());
            throw new FlowException("排他网关没有找到满足条件的分支且无默认路径");
        }
    }

    private boolean evaluateCondition(Connection connection, FlowContext context) {
        String expression = connection.getExpression();
        if (expression == null || expression.trim().isEmpty()) {
            return true; // 没有条件表达式时默认为true
        }
        
        try {
            Map<String, Object> evalContext = new HashMap<>();
            evalContext.putAll(context.getVariables());
            evalContext.putAll(context.getParams());
            return scriptEngineService.evaluateCondition(expression, evalContext);
        } catch (Exception e) {
            log.error("条件表达式执行异常: {}", e.getMessage());
            throw new FlowException("条件表达式执行异常: " + e.getMessage());
        }
    }

    private Object constructResponse(FlowContext context, Results results) {
        // 提取指定的字段
        Map<String, Object> rawResult = new HashMap<>();
        Object rawResultObj = null;

        if(results != null && results.getResult()!=null && results.getResult().size() != 0){

            int resultCount = results.getResult().size();

            for (int i =0 ;results.getResult()!=null &&  i< results.getResult().size() ;i++ ) {
                ResultItem result = results.getResult().get(i);
                String stepType = result.getStepType();
                StepType sType = StepType.UNSTEP;
                if (stepType != null) {
                    sType = StepType.getByValue(Integer.parseInt(stepType));
                }
                String fieldName = result.getName();
                // 先从变量中获取
                Object fieldValue = context.getVariable(fieldName);
                if (fieldValue != null) {
                    if(sType.equals(StepType.STEP)){ //提级
                        if(resultCount == 1 && fieldValue instanceof List){
                            rawResultObj = fieldValue;
                        }else{
                            FieldValueExtractor.extractFields(fieldValue, rawResult);
                        }

                    }else{
                        rawResult.put(fieldName, fieldValue);
                    }
                } else {
                    // 如果变量中没有，从参数中获取
                    Object paramValue = context.getParam(fieldName);
                    if(sType.equals(StepType.STEP)){ //提级
                        if(resultCount == 1 && fieldValue instanceof List){
                            rawResultObj = fieldValue;
                        }else{
                            FieldValueExtractor.extractFields(fieldValue, rawResult);
                        }
                    }else{
                        rawResult.put(fieldName, paramValue);
                    }
                }
            }
        }

        // 获取响应结构类型
        String responseStruct = results.getResponseStruct();
        Integer responseStructInt = Integer.parseInt(responseStruct);
        ResponseStructType responseStructType = ResponseStructType.fromValue(responseStructInt);
        // 根据不同的响应结构类型构造返回数据
        switch (responseStructType) {
            case STANDARD: // 标准结构
                if(rawResultObj != null){
                    return Result.success(rawResultObj);
                }
                return Result.success(rawResult);
            case SIMPLE_OBJECT: // 简单对象
                return rawResult;
            default:
                return rawResult;
        }
    }
} 