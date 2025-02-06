package cc.xiaonuo.flow.model;

import lombok.Data;

@Data
public class AssertionResult {
    private String type;
    private String field;
    private Object expectedValue;
    private Object actualValue;
    private boolean success;
    private String message;
} 