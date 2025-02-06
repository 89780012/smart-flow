package cc.xiaonuo.flow.method;

import cn.hutool.core.date.DateUtil;
import cc.xiaonuo.common.enums.DataType;

import java.math.BigDecimal;
import java.util.Date;

public class CommonMethod {

    public Object convertConstantValue(String value, DataType dataType) {
        try {
            switch (dataType) {
                case STRING:
                    return value;
                case INTEGER:
                    return Integer.parseInt(value);
                case FLOAT:
                    return Float.parseFloat(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                case DATE:
                    return parseDate(value);
                case LONG:
                    return Long.parseLong(value);
                case ARRAY:
                case OBJECT:
                    return value;
                case BigDecimal:
                    return new BigDecimal(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            return value;
        }
    }

    public Date parseDate(String DateStr) {
        return DateUtil.parse(DateStr);
    }
}
