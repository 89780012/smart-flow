package cc.xiaonuo.common.enums;

public enum DatabaseType {
    MYSQL("mysql"),
    ORACLE("oracle"),
    POSTGRESQL("postgresql"),
    SQLSERVER("sqlserver");

    private final String type;

    DatabaseType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static DatabaseType fromString(String type) {
        for (DatabaseType dbType : DatabaseType.values()) {
            if (dbType.type.equalsIgnoreCase(type)) {
                return dbType;
            }
        }
        throw new IllegalArgumentException("Unsupported database type: " + type);
    }
} 