package cc.xiaonuo.flow.method;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import cn.hutool.core.date.DateUtil;
import cc.xiaonuo.common.enums.DateFormatType;
import cc.xiaonuo.common.exception.FlowException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@FlowComponent("flow-dateUtil")
public class DateUtils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        //获取操作类型 找到seq为1的参数
        PropertyParam operationType = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        String operationTypeValue = operationType.getVal();

        //获取日期格式 找到seq为2的参数
        PropertyParam dateFormat = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        String dateFormatValue = dateFormat.getVal();
        //从DateFormatType中获取日期格式
        DateFormatType dateFormatType = DateFormatType.valueOf(dateFormatValue);

        //根据操作类型进行赋值
        switch (operationTypeValue) {
            case "GET_CURRENT_DATE":
                //获取第三个参数的值
                PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
                String bindParamValue = bindParam.getVal();
                Date currentDate = DateUtil.date();
                String dateFormatStr = DateUtil.format(currentDate, dateFormatType.getFormat());
                context.setVariable(bindParamValue, dateFormatStr);
                break;
            case "FORMAT_DATE":
                PropertyParam inParam = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
                String inParamValue = inParam.getVal();

                PropertyParam bindParam2 = params.stream().filter(p -> p.getSeq().equals("4")).findFirst().orElse(null);
                String bindParamValue2 = bindParam2.getVal();

                Object inParamValueDate =  CommonUtils.getParam(inParamValue, context);
                if(!(inParamValueDate instanceof Date)){
                    throw new FlowException(inParamValue + "参数类型错误,不是Date或DateTime格式");
                }
                String dateFormatStr2 = DateUtil.format((Date)inParamValueDate, dateFormatType.getFormat());
                context.setVariable(bindParamValue2, dateFormatStr2);
                break;
        }
    }



}
