package cc.xiaonuo.flow.convert;

import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.BizDefinition;
import cc.xiaonuo.flow.model.Param;
import com.alibaba.fastjson.JSON;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.common.enums.DataType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class FlowParamConvert {

    public void convertDataType(BizDefinition biz, FlowContext context) {
        if (biz.getParams() == null) {
            return;
        }

        if(biz.getParams().getQueryParams() !=null){
            for (Param param : biz.getParams().getQueryParams().getQueryParam()) {
                convertDataType(param, context);
            }
        }

        if(biz.getParams().getBodyParams() !=null){
            for (Param param : biz.getParams().getBodyParams().getBodyParam()) {
                convertDataType(param, context);
            }
        }
    }

    public void convertDataType(Param param, FlowContext context) {
        Object value = context.getParam(param.getName());
        if (value == null) {
            return;
        }
        try {
            int typeValue = Integer.parseInt(param.getType());
            DataType dataType = DataType.getByValue(typeValue);
            if (dataType == null) {
                throw new FlowException(String.format("参数[%s]的数据类型[%s]不支持",
                        param.getName(), param.getType()));
            }

            Object convertedValue = null;
            switch (dataType) {
                case STRING:
                    convertedValue = convertToString(value);
                    break;
                case INTEGER:
                    convertedValue = convertToInteger(value);
                    break;
                case FLOAT:
                    convertedValue = convertToFloat(value);
                    break;
                case DOUBLE:
                    convertedValue = convertToDouble(value);
                    break;
                case LONG:
                    convertedValue = convertToLong(value);
                    break;
                case BOOLEAN:
                    convertedValue = convertToBoolean(value);
                    break;
                case ARRAY:
                    convertedValue = convertToArray(value);
                    break;
                case OBJECT:
                    convertedValue = convertToObject(value);
                    break;
                case DATE:
                    convertedValue = convertToDate(value);
                    break;
                case BigDecimal:
                    convertedValue = convertToBigDecimal(value);
                    break;
                default:
                    throw new FlowException(String.format("参数[%s]的数据类型[%s]不支持",
                            param.getName(), dataType.getDisplayName()));
            }

            context.getParams().put(param.getName(), convertedValue);

        } catch (NumberFormatException e) {
            throw new FlowException(String.format("参数[%s]的数据类型值[%s]无效",
                    param.getName(), param.getType()));
        } catch (Exception e) {
            throw new FlowException(String.format("参数[%s]转换失败: %s",
                    param.getName(), e.getMessage()));
        }
    }

    private String convertToString(Object value) {
        return value.toString();
    }

    private Integer convertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private Float convertToFloat(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.parseFloat(value.toString());
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private Long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String strValue = value.toString().toLowerCase();
        return "true".equals(strValue) || "1".equals(strValue);
    }

    private Object convertToArray(Object value) {
        if (value instanceof List || value.getClass().isArray()) {
            return value;
        }
        if (value instanceof String) {
            return JSON.parseArray((String)value);
        }
        throw new FlowException("无法将值转换为数组类型");
    }

    private Object convertToObject(Object value) {
        if (value instanceof Map) {
            return value;
        }
        if (value instanceof String) {
            return JSON.parseObject((String)value);
        }
        throw new FlowException("无法将值转换为对象类型");
    }

    private Date convertToDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(value.toString());
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(value.toString());
            } catch (ParseException ex) {
                throw new FlowException("无法将值转换为日期类型");
            }
        }
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }
}
