package cc.xiaonuo.common.enums;

public enum ResponseStructType {
    STANDARD(1, "标准结构(code/message/data)"),
    SIMPLE_OBJECT(2, "简单对象"),
    ARRAY(3, "数组结构");

    private final int value;
    private final String displayName;

    ResponseStructType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static ResponseStructType fromValue(Integer responseStructInt) {
        if (responseStructInt == null) {
            return null;
        }
        for (ResponseStructType responseStructType : ResponseStructType.values()) {
            if (responseStructType.getValue() == responseStructInt) {
                return responseStructType;
            }
        }
        return null;
    }


    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}