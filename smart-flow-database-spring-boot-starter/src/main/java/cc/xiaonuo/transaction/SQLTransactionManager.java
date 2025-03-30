package cc.xiaonuo.transaction;

import cc.xiaonuo.common.trans.FlowTransactionManager;
import cc.xiaonuo.database.cache.DBCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SQLTransactionManager implements FlowTransactionManager {
    private static final ThreadLocal<List<Connection>> TRANSACTION_CONNECTIONS = new ThreadLocal<>();
    public void beginTransaction(Connection conn) throws SQLException {
        if (TRANSACTION_CONNECTIONS.get() == null) {
            TRANSACTION_CONNECTIONS.set(new ArrayList<>());
        }

        conn.setAutoCommit(false);
        if(!TRANSACTION_CONNECTIONS.get().contains(conn)){
            TRANSACTION_CONNECTIONS.get().add(conn);
        }
    }

    public void commit() throws SQLException {
        List<Connection> connections = TRANSACTION_CONNECTIONS.get();
        if (connections != null) {
            SQLException exception = null;
            for (Connection conn : connections) {
                try {
                    conn.commit();
                    log.debug("Transaction committed successfully");
                } catch (SQLException e) {
                    log.error("Transaction commit failed", e);
                    exception = e;
                    // 发生异常时尝试回滚
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        log.error("Rollback failed after commit error", rollbackEx);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    public void rollback() {
        List<Connection> connections = TRANSACTION_CONNECTIONS.get();
        if (connections != null) {
            for (Connection conn : connections) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    log.error("Transaction rollback failed", e);
                }
            }
        }
    }

    public void clear(String flowId) {
        List<Connection> connections = TRANSACTION_CONNECTIONS.get();
        if (connections != null) {
            for (Connection conn : connections) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Close connection failed", e);
                }
            }
            TRANSACTION_CONNECTIONS.remove();

            //刪除key以flowId开头的数据
            DBCache.connectionMap.remove(flowId);
        }
    }
}