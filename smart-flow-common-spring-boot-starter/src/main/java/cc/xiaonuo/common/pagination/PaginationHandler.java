package cc.xiaonuo.common.pagination;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import cc.xiaonuo.common.enums.DatabaseType;

import java.util.Map;

public interface PaginationHandler {
    DatabaseType databaseType();
    String wrapPaginationSql(String originalSql, int pageNo, int pageSize);
    String getCountSql(String originalSql);
    Map<String, Object> handlePaginationResult(Object result, long total, int pageNo, int pageSize, SmartFlowConfig config);
} 