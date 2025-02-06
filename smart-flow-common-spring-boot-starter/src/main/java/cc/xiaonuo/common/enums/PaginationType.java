package cc.xiaonuo.common.enums;

public enum PaginationType {
    YES(1, "是"),
    NO(2, "否");

    private final int value;
    private final String displayName;

    PaginationType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}