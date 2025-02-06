package cc.xiaonuo.common.manager;

import cc.xiaonuo.common.sqlhandler.SqlMeta;
import cc.xiaonuo.common.enums.DatabaseType;
import cc.xiaonuo.common.pagination.PaginationHandler;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface AbstractDBManager {

    DataSource getDataSource(String dataSourceId);

    boolean hasDataSource();

    DatabaseType getDatabaseType(String dataSourceKey);

    String getDataSourceType(String dataSourceKey);

    Object handlePagination(PaginationHandler handler, SqlMeta sqlMeta, DataSource dataSource,
                            DatabaseType dbType, int pageNo, int pageSize);

    List<Map<String, Object>> executeQuery(DataSource dataSource, SqlMeta sqlMeta);

    Long executeCount(DataSource dataSource, SqlMeta sqlMeta);

    /**
     * 执行更新操作
     * @param connection 数据源
     * @param sqlMeta SQL元数据
     * @return 受影响的行数
     */
    int executeUpdate(Connection connection, SqlMeta sqlMeta);


}

