package cc.xiaonuo.flow.engine;

import cc.xiaonuo.flow.model.BizDefinition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class FlowContext {
    private String flowId;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public Map<String, Object> variables;
    public Map<String, Object> params;

    private BizDefinition bizDefinition;

    public FlowContext(String flowId, Map<String, Object> params,HttpServletRequest request, HttpServletResponse response) {
        this.flowId = flowId;
        this.params = params;
        this.variables = new HashMap<>();
        this.request = request;
        this.response = response;
    }

    public void setBizDefinition(BizDefinition bizDefinition) {
        this.bizDefinition = bizDefinition;
    }

    public BizDefinition getBizDefinition() {
        return bizDefinition;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }
    
    public Object getVariable(String key) {
        return variables.get(key);
    }
    
    public Object getParam(String key) {
        return params.get(key);
    }

}