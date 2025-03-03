package cc.xiaonuo.flow.valid;

import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.BizDefinition;
import cc.xiaonuo.flow.model.Param;
import cc.xiaonuo.flow.model.QueryParams;
import org.springframework.stereotype.Component;


@Component
public class FlowParamValidator {
    
    /**
     * 验证流程参数
     * @param biz 流程定义
     * @param context 流程上下文
     */
    public void validate(BizDefinition biz, FlowContext context) {
        if (biz.getParams() == null ) {
            return;
        }

        if(biz.getParams().getQueryParams() != null){
            for (Param param : biz.getParams().getQueryParams().getQueryParam()) {
                validateRequired(param, context);
            }
        }

        if(biz.getParams().getBodyParams() != null){
            for (Param param : biz.getParams().getBodyParams().getBodyParam()) {
                validateRequired(param, context);
            }
        }
    }

    private void validateRequired(Param param, FlowContext context) {
        if ("1".equals(param.getRequired()) && !context.getParams().containsKey(param.getName())) {
            throw new FlowException(String.format("缺少必填参数: %s", param.getName()));
        }
    }

}