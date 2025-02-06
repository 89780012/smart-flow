package cc.xiaonuo.flow.method;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import cn.hutool.core.util.RandomUtil;
import cc.xiaonuo.common.enums.RandomType;
import org.springframework.stereotype.Component;

import java.util.List;

@FlowComponent("flow-randomUtil")
public class RandomlUtils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }
        //获取随机类型
        PropertyParam randomTypeParam = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        RandomType randomType = RandomType.valueOf(randomTypeParam.getVal());

        //获取长度
        PropertyParam lenParam = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        int len = lenParam.getVal() == null ? 0 : Integer.parseInt(lenParam.getVal());

        //获取绑定值
        PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("3")).findFirst().orElse(null);
        String bindParamValue = bindParam.getVal();

        switch (randomType) {
            case NUMBER:
                String randomNum = RandomUtil.randomNumbers(len);
                context.setVariable(bindParamValue, randomNum);
                break;
            case STRING:
                String randomStr = RandomUtil.randomString(len);
                context.setVariable(bindParamValue, randomStr);
                break;
        }
    }
}
