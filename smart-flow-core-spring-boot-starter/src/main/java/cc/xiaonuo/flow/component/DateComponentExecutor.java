package cc.xiaonuo.flow.component;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.CusComponent;
import cc.xiaonuo.flow.model.Property;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FlowComponent("flow-date")
public class DateComponentExecutor extends AbstractComponentExecutor{

    @Override
    public String getType() {
        return "flow-date";
    }
    
    @Override
    protected void doExecute(CusComponent cusComponent, FlowContext context) {
        Property property = cusComponent.getProperty();
        if (property == null) {
            property = new Property();
        }
        property.setBeanRef("flow-dateUtil");
        property.setMethod("cc.xiaonuo.flow.method.DateUtils#exec");
        cusComponent.setProperty(property);
        super.execBeanRef(cusComponent, context);
    }
}