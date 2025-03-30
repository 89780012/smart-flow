package cc.xiaonuo.flow.component;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import cc.xiaonuo.common.enums.DatabaseType;
import cc.xiaonuo.common.enums.ReturnType;
import cc.xiaonuo.common.enums.SQLOperationType;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.common.manager.AbstractDBManager;
import cc.xiaonuo.common.pagination.PaginationHandler;
import cc.xiaonuo.common.sqlhandler.SqlMeta;
import cc.xiaonuo.common.trans.FlowTransactionManager;
import cc.xiaonuo.common.utils.SpringContextHolder;
import cc.xiaonuo.database.cache.DBCache;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.method.CommonUtils;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.parser.orange.engine.DynamicSqlEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@FlowComponent("flow-sql")
public class SqlComponentExecutor extends AbstractComponentExecutor {

    private static final String LOG_PREFIX = "【Flow SQL】";

    @Autowired(required = false)
    private List<AbstractDBManager> dataSourceManagerList;
    private DynamicSqlEngine dynamicSqlEngine = new DynamicSqlEngine();
    private SmartFlowConfig config;

    @Autowired(required = false)
    private List<PaginationHandler> paginationHandlers;

    @Autowired(required = false)
    private List<FlowTransactionManager> flowTransactionManagers;

    private Map<DatabaseType, PaginationHandler>  handlerMap = new HashMap<>();


    @PostConstruct
    public void init() {
        if(paginationHandlers == null){
            return;
        }

        if(handlerMap != null) {
            for(PaginationHandler handler : paginationHandlers) {
                handlerMap.put(handler.databaseType(), handler);
            }
        }
        config = SpringContextHolder.getBean(SmartFlowConfig.class);

    }

    @Override
    public String getType() {
        return "sql";
    }

    @Override
    protected void doExecute(CusComponent cusComponent, FlowContext context) {
        if (dataSourceManagerList == null || dataSourceManagerList.isEmpty()) {
            throw new FlowException("未找到数据源管理器");
        }

        // 获取数据源
        String dataSourceKey = cusComponent.getProperty().getDataSourceKey();
        String bindKey = cusComponent.getProperty().getBindKey();
        String paginationType = cusComponent.getProperty().getPaginationType();
        String operationType = cusComponent.getProperty().getOperationType();

        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            dataSourceKey = "default";
        }

        // 获取SQL语句
        String sqlText = cusComponent.getProperty().getSql();
        if (sqlText == null || sqlText.trim().isEmpty()) {
            throw new FlowException("SQL语句不能为空");
        }

        sqlText = removeCDATA(sqlText);
        log.info("{} DataSource: {}", LOG_PREFIX, dataSourceKey);
        log.info("{} Original SQL: {}", LOG_PREFIX, sqlText);

        try {
            String flowId = context.getFlowId();
            Map<String, Connection> dbMap = DBCache.connectionMap.get(flowId);
            Connection conn = null;
            DataSource dataSource = dataSourceManagerList.get(0).getDataSource(dataSourceKey);

            if(dbMap == null) {
                Map<String,Connection> tempDbMap = new HashMap<>();
                // 获取数据源
                conn = dataSource.getConnection();
                tempDbMap.put(dataSourceKey,conn);
                DBCache.connectionMap.put(flowId,tempDbMap);
            }else if(dbMap.get(dataSourceKey) == null) {
                conn = dataSource.getConnection();
                dbMap.put(dataSourceKey, conn);
                DBCache.connectionMap.put(flowId,dbMap);
            }else{
                conn = dbMap.get(dataSourceKey);
            }

            DatabaseType dbType = dataSourceManagerList.get(0).getDatabaseType(dataSourceKey);
            if(context.getBizDefinition().getGlobal_sql_transaction().equals("true")){  //开启事务,则流程提交后才提交
                flowTransactionManagers.get(0).beginTransaction(conn);
            }else{
                conn.setAutoCommit(true);  //每个sql执行完就提交
            }

            // 解析动态SQL
            Map<String, Object> params = new HashMap<>();
            params.putAll(context.getParams());
            params.putAll(context.getVariables());
            SqlMeta sqlMeta = dynamicSqlEngine.parse(sqlText, params);

            log.info("{} Parameters:{} ", LOG_PREFIX, sqlMeta.getJdbcParamValues());
            log.info("{} sqlMeta:{} ", LOG_PREFIX, sqlMeta.getSql());
            ReturnType returnType = ReturnType.LIST_MAP;
            String returnTypestr = cusComponent.getProperty().getReturnType();
            if (returnTypestr != null &&!returnTypestr.trim().isEmpty()) {
                returnType = ReturnType.getByValue(Integer.parseInt(returnTypestr));
            }

            log.info("{} Return Type:{}",LOG_PREFIX,returnType);
            String pageNoKey = config.getSettings().getProperty("sql_pageNo");
            String pageSizeKey = config.getSettings().getProperty("sql_pageSize");
            int pageNo = getPageNo(pageNoKey,context);
            int pageSize = getPageSize(pageSizeKey,context);

            Object result;
            // 根据操作类型执行不同的SQL操作
            SQLOperationType sqlOperationType = SQLOperationType.getByKey(operationType);
            if (sqlOperationType == null) {
                sqlOperationType = SQLOperationType.QUERY; // 默认查询
            }

            switch (sqlOperationType) {
                case UPDATE:
                case INSERT:
                case DELETE:
                    // 执行更新操作
                    int affectedRows = dataSourceManagerList.get(0).executeUpdate(conn, sqlMeta);
                    result = affectedRows;
                    log.info("{} Affected rows: {}", LOG_PREFIX, affectedRows);
                    break;
                    
                case QUERY:
                default:
                    // 原有的查询逻辑
                    if ("YES".equalsIgnoreCase(paginationType)) {
                        PaginationHandler handler = handlerMap.get(dbType);
                        if (handler == null) {
                            throw new FlowException("Unsupported database type for pagination: " + dbType);
                        }
                        result = dataSourceManagerList.get(0).handlePagination(handler, sqlMeta, dataSource, dbType, pageNo, pageSize);
                    } else {
                        List<Map<String, Object>> rawResult = dataSourceManagerList.get(0).executeQuery(dataSource, sqlMeta);
                        result = convertResult(rawResult, returnType);
                    }
                    break;
            }

            context.setVariable(bindKey, result);
        } catch (Exception e) {
            flowTransactionManagers.get(0).rollback();
            log.error("{} Execution Error", LOG_PREFIX);
            log.error("{} SQL: {}", LOG_PREFIX, sqlText);
            log.error("{} Error Message: {}", LOG_PREFIX, e.getMessage());
            throw new FlowException("SQL执行异常: " + e.getMessage());
        }
    }

    private String removeCDATA(String sql) {
        sql = sql.trim();
        if (sql.startsWith("<![CDATA[") && sql.endsWith("]]>")) {
            sql = sql.substring(9, sql.length() - 3);
        }
        return sql;
    }
    
    private Object convertResult(List<Map<String, Object>> rawResult, ReturnType returnType) {
        if (rawResult == null || rawResult.isEmpty()) {
            log.info("{} No Data Found", LOG_PREFIX);
            switch (returnType) {
                case LIST_MAP:
                case LIST_STRING:
                case LIST_INTEGER:
                case LIST_FLOAT:
                case LIST_DOUBLE:
                    return new ArrayList<>();
                case MAP:
                    return null; //写sql ，如果单条记录也没有，应该返回null, 而不是空Map
                case STRING:
                    return "";
                case INTEGER:
                    return 0;
                case LONG:
                    return 0L;
                case BOOLEAN:
                    return false;
                default:
                    return new ArrayList<>();
            }
        }
    
        Object result;
        switch (returnType) {
            case LIST_MAP:
                result = rawResult;
                break;
            case MAP:
                result = rawResult.get(0);
                break;
            case LIST_STRING:
                result = rawResult.stream()
                    .flatMap(map -> map.values().stream())
                    .map(Object::toString)
                    .collect(Collectors.toList());
                break;
            case LIST_INTEGER:
                result = rawResult.stream()
                        .flatMap(map -> map.values().stream())
                        .map(item -> {
                            return Integer.parseInt(item.toString());
                        })
                        .collect(Collectors.toList());
                break;
            case LIST_FLOAT:
                result = rawResult.stream()
                        .flatMap(map -> map.values().stream())
                        .map(item -> {
                            return Float.parseFloat(item.toString());
                        })
                        .collect(Collectors.toList());
                break;
            case LIST_DOUBLE:
                result = rawResult.stream()
                        .flatMap(map -> map.values().stream())
                        .map(item -> {
                            return Double.parseDouble(item.toString());
                        })
                        .collect(Collectors.toList());
                break;
            case STRING:
                result = rawResult.get(0).values().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("");
                break;
            case INTEGER:
                result = rawResult.get(0).values().stream()
                    .findFirst()
                    .map(val -> Integer.parseInt(val.toString()))
                    .orElse(0);
                break;
            case LONG:
                result = rawResult.get(0).values().stream()
                    .findFirst()
                    .map(val -> Long.parseLong(val.toString()))
                    .orElse(0L);
                break;
            case BOOLEAN:
                result = rawResult.get(0).values().stream()
                    .findFirst()
                    .map(val -> Boolean.parseBoolean(val.toString()))
                    .orElse(false);
                break;
            default:
                result = rawResult;
        }
        
        log.debug("{} Converted Result Type: {}", LOG_PREFIX, result.getClass().getSimpleName());
        return result;
    }



    private int getPageNo(String pageNoKey,FlowContext context) {
        Object pageNo = CommonUtils.getParam(pageNoKey,context);
        return pageNo != null ? Integer.parseInt(pageNo.toString()) : 1;
    }

    private int getPageSize(String pageSizeKey,FlowContext context) {
        Object pageSize = context.getParams().get(pageSizeKey);
        return pageSize != null ? Integer.parseInt(pageSize.toString()) : 10;
    }


}