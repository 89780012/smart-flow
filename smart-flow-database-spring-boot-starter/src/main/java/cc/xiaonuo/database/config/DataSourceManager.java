package cc.xiaonuo.database.config;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import cc.xiaonuo.common.cache.PluginCache;
import cc.xiaonuo.common.enums.DatabaseType;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.common.manager.AbstractDBManager;
import cc.xiaonuo.common.pagination.PaginationHandler;
import cc.xiaonuo.common.sqlhandler.SqlMeta;
import cc.xiaonuo.common.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceManager implements AbstractDBManager {
    
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private final Map<DataSource, JdbcTemplate> jdbcTemplateMap = new ConcurrentHashMap<>();

    private String defaultDataSourceId;
    private boolean initialized = false;

    private SmartFlowConfig config;


    public DataSourceManager() {
        config = SpringContextHolder.getBean(SmartFlowConfig.class);
    }
    
    @PostConstruct
    public void init() {
        // 检查是否有数据源配置
        if (config.getDataSources() == null || config.getDataSources().isEmpty()) {
            log.info("未配置数据源,跳过DataSourceManager初始化");
            return;
        }

        // 设置默认数据源
        this.defaultDataSourceId = config.getSettings().getProperty("default");
        
        // 初始化所有数据源
        config.getDataSources().forEach((id, dsConfig) -> {
            try {
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setDriverClassName(dsConfig.getProperty("driverClassName"));
                hikariConfig.setJdbcUrl(dsConfig.getProperty("url"));
                hikariConfig.setUsername(dsConfig.getProperty("username"));
                hikariConfig.setPassword(dsConfig.getProperty("password"));
                
                // 连接池核心配置
                hikariConfig.setMaximumPoolSize(50);         // 最大连接数
                hikariConfig.setMinimumIdle(20);             // 最小空闲连接
                hikariConfig.setIdleTimeout(600000);         // 空闲连接超时时间，10分钟
                hikariConfig.setMaxLifetime(1800000);        // 连接最大生命周期，30分钟
                hikariConfig.setConnectionTimeout(30000);     // 连接超时时间，30秒
                hikariConfig.setValidationTimeout(5000);      // 连接有效性检查超时时间，5秒

                // 性能优化配置
                hikariConfig.setAutoCommit(false);           // 关闭自动提交
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");        // 开启预编译语句缓存
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");      // 预编译语句缓存大小
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // 预编译语句最大长度
                hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");    // 启用服务器端预编译

                // 连接池监控配置
                hikariConfig.setPoolName("HikariPool-" + id);  // 设置连接池名称

                DataSource dataSource = new HikariDataSource(hikariConfig);
                dataSources.put(id, dataSource);
                log.info("初始化数据源成功: {}", id);
            } catch (Exception e) {
                log.error("初始化数据源失败: {}", id, e);
            }
        });
        
        initialized = true;
    }
    
    public boolean hasDataSource() {
        return initialized && !dataSources.isEmpty();
    }
    
    public DataSource getDataSource(String dataSourceId) {
        if (!initialized) {
            throw new FlowException("DataSourceManager未初始化,请检查是否配置了数据源");
        }
        
        if (dataSourceId == null || dataSourceId.trim().isEmpty()) {
            dataSourceId = defaultDataSourceId;
        }
        
        DataSource dataSource = dataSources.get(dataSourceId);
        if (dataSource == null) {
            throw new FlowException("数据源未找到: " + dataSourceId);
        }
        return dataSource;
    }

    public JdbcTemplate getJdbcTemplate(DataSource dataSource){
        if(jdbcTemplateMap.containsKey(dataSource)){
            return jdbcTemplateMap.get(dataSource);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplateMap.put(dataSource,jdbcTemplate);
        return jdbcTemplate;
    }
    
    public DatabaseType getDatabaseType(String dataSourceKey) {
        // 从配置中获取数据源类型
        String type = getDataSourceType(dataSourceKey);
        return DatabaseType.fromString(type);
    }

    public String getDataSourceType(String dataSourceKey) {
        return PluginCache.dataSourceTypeMap.get(dataSourceKey);
    }

    public List<Map<String, Object>> executeQuery(DataSource dataSource, SqlMeta sqlMeta) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSource);
        if (sqlMeta.getJdbcParamValues().isEmpty()) {
            return jdbcTemplate.queryForList(sqlMeta.getSql());
        }
        return jdbcTemplate.queryForList(sqlMeta.getSql(), sqlMeta.getJdbcParamValues().toArray());
    }

    public Long executeCount(DataSource dataSource, SqlMeta sqlMeta) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSource);
        if (sqlMeta.getJdbcParamValues().isEmpty()) {
            return jdbcTemplate.queryForObject(sqlMeta.getSql(), Long.class);
        }
        return jdbcTemplate.queryForObject(sqlMeta.getSql(), Long.class,
                sqlMeta.getJdbcParamValues().toArray());
    }

    /**
     * 执行更新操作
     * @param conn 数据源
     * @param sqlMeta SQL元数据
     * @return 受影响的行数
     */
    @Override
    public int executeUpdate(Connection conn, SqlMeta sqlMeta) {
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlMeta.getSql());

            List<Object> params = sqlMeta.getJdbcParamValues();
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            return stmt.executeUpdate();
        }catch (Exception e){
            throw new FlowException("sql更新操作执行错误:" + e.getMessage());
        }

    }
    public Object handlePagination(PaginationHandler handler,SqlMeta sqlMeta, DataSource dataSource,
                                     DatabaseType dbType,int pageNo, int pageSize) {

        if (handler == null) {
            throw new FlowException("Unsupported database type for pagination: " + dbType);
        }
        // 获取总记录数
        String countSql = handler.getCountSql(sqlMeta.getSql());
        SqlMeta countSqlMeta = new SqlMeta(countSql, sqlMeta.getJdbcParamValues());
        Long total = executeCount(dataSource, countSqlMeta);

        // 构建分页SQL
        String paginationSql = handler.wrapPaginationSql(sqlMeta.getSql(), pageNo, pageSize);
        SqlMeta paginationSqlMeta = new SqlMeta(paginationSql, sqlMeta.getJdbcParamValues());

        // 执行分页查询
        List<Map<String, Object>> records = executeQuery(dataSource, paginationSqlMeta);

        // 封装分页结果
        return handler.handlePaginationResult(records, total,pageNo,pageSize, config);
    }



} 