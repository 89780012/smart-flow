package cc.xiaonuo.common.enums;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static HttpMethod fromValue(String value) {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.getValue().equals(value)) {
                return method;
            }
        }
        return GET; // 默认返回 GET
    }
}
