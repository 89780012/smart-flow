package cc.xiaonuo.common.trans;

import java.sql.Connection;
import java.sql.SQLException;

public interface FlowTransactionManager {

    void beginTransaction(Connection conn) throws SQLException;

    void commit() throws SQLException;

    void rollback();

    void clear(String flowId);
}
