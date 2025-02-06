package cc.xiaonuo.database.cache;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBCache {

    public static Map<String, Map<String,Connection>> connectionMap = new ConcurrentHashMap<String, Map<String,Connection>>();


}
