package cc.xiaonuo.common.enums;

public enum ReturnType {
    LIST_MAP(0,"List<Map<String, Object>>"),
    MAP(1,"Map<String, Object>"),
    LIST_STRING(2,"List<String>"),
    STRING(3,"String"),
    INTEGER(4,"Integer"),
    LONG(5,"Long"),
    BOOLEAN(6,"Boolean"),
    LIST_INTEGER(7,"LIST<Integer>"),
    LIST_FLOAT(8,"LIST<FLOAT>"),
    LIST_DOUBLE(9,"LIST<DOUBLE>");


    private final int value;
    private final String displayName;

    ReturnType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String[] getDisplayValues() {
        ReturnType[] values = ReturnType.values();
        String[] displayNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayNames[i] = values[i].getDisplayName();
        }
        return displayNames;
    }


    public static ReturnType getByValue(int value) {
        for (ReturnType type : ReturnType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的返回类型: " + value);
    }

    public int getValue() {
        return value;
    }
}