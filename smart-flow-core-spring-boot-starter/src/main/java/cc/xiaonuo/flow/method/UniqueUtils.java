package cc.xiaonuo.flow.method;

import cc.xiaonuo.common.cache.PluginCache;
import cc.xiaonuo.common.enums.UniqueIdType;
import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import cc.xiaonuo.flow.utils.SnowFlakeIdWorker;
import org.springframework.stereotype.Component;

import java.util.List;

@FlowComponent("flow-uniqueUtil")
public class UniqueUtils extends CommonUtils{

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            return;
        }

        //获取生成类型
        PropertyParam generatorTypeParam = params.stream().filter(p -> p.getSeq().equals("1")).findFirst().orElse(null);
        UniqueIdType generatorType = UniqueIdType.valueOf(generatorTypeParam.getVal());

        //获取绑定值
        PropertyParam bindParam = params.stream().filter(p -> p.getSeq().equals("2")).findFirst().orElse(null);
        String bindParamValue = bindParam.getVal();

        //根据生成类型进行生成
        switch (generatorType) {
            case UUID:
                context.setVariable(bindParamValue, java.util.UUID.randomUUID().toString());
                break;
            case SNOWFLAKE:
                SnowFlakeIdWorker snowFlakeIdWorker = new SnowFlakeIdWorker(PluginCache.unique_roomid, 0);
                context.setVariable(bindParamValue, String.valueOf(snowFlakeIdWorker.nextId()));
                break;
        }
    }
}
