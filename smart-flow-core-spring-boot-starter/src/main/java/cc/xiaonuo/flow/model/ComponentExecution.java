package cc.xiaonuo.flow.model;

import lombok.Data;
import java.util.Date;

@Data
public class ComponentExecution {
    private String componentId;
    private String componentName;
    private Date startTime;
    private Date endTime;
    private ExecutionStatus status;
    private Object result;
    private String error;
} 