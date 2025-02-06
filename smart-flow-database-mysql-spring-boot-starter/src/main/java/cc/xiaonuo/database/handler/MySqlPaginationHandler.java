package cc.xiaonuo.database.handler;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import cc.xiaonuo.common.enums.DatabaseType;
import cc.xiaonuo.common.pagination.PaginationHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlPaginationHandler implements PaginationHandler {

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public String wrapPaginationSql(String originalSql, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return originalSql + " LIMIT " + offset + "," + pageSize;
    }

    @Override
    public String getCountSql(String originalSql) {
        return "SELECT COUNT(*) FROM (" + originalSql + ") t";
    }

    @Override
    public Map<String, Object> handlePaginationResult(Object result, long total, int pageNo, int pageSize, SmartFlowConfig config) {
        Map<String, Object> paginationResult = new HashMap<>();
        paginationResult.put(config.getSettings().getProperty("sql_records"), result);
        paginationResult.put(config.getSettings().getProperty("sql_total"), total);
        paginationResult.put(config.getSettings().getProperty("sql_pageNo"), pageNo);
        paginationResult.put(config.getSettings().getProperty("sql_pageSize"), pageSize);
        paginationResult.put(config.getSettings().getProperty("sql_pages"), (total + pageSize - 1) / pageSize);
        return paginationResult;
    }
} 