package cc.xiaonuo.web.exception;

import cc.xiaonuo.common.bean.Result;
import cc.xiaonuo.common.exception.FlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FlowException.class)
    public Result handleFlowException(FlowException e) {
        e.printStackTrace();
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error(500, "系统异常：" + e.getMessage());
    }
} 