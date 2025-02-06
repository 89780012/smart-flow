package cc.xiaonuo.flow.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class FlowExecution {
    private String flowId;
    private Date startTime;
    private Date endTime;
    private ExecutionStatus status;
    private Object result;
    private String error;
    private List<ComponentExecution> componentExecutions = new ArrayList<>();
} 