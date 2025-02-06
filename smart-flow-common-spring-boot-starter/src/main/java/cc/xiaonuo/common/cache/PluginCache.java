package cc.xiaonuo.common.cache;

import cc.xiaonuo.common.sqlhandler.node.SqlNode;

import java.util.concurrent.ConcurrentHashMap;


public class PluginCache {

    public static ConcurrentHashMap<String, SqlNode> nodeCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, SqlNode> getNodeCache() {
        return nodeCache;
    }

    /* 雪花算法id */
    public static int unique_roomid = 1;
    public static int unique_workid = 0;

    // 用于存储数据源id和类型
    public static ConcurrentHashMap<String, String> dataSourceTypeMap = new ConcurrentHashMap<>();
    


}
