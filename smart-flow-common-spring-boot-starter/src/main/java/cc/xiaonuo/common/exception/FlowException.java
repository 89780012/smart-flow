package cc.xiaonuo.common.exception;

import lombok.Getter;

@Getter
public class FlowException extends RuntimeException {
    private final int code;

    public FlowException(String message) {
        super(message);
        this.code = 500;
    }

    public FlowException(int code, String message) {
        super(message);
        this.code = code;
    }
} 