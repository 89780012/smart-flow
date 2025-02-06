package cc.xiaonuo.flow.method;

import cc.xiaonuo.flow.annotation.FlowComponent;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.model.PropertyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
@FlowComponent("flow-logUtil")
public class LogUtils extends CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);
    private static final String LOG_PREFIX = "【Flow Log】";
    private static final String PARAM_FORMAT = "%-20s";

    public void exec(List<PropertyParam> params, FlowContext context) {
        if (params == null || params.isEmpty()) {
            logger.info("{} No parameters to log", LOG_PREFIX);
            return;
        }

        try {
            for (PropertyParam param : params) {
                String key = param.getVal();
                Object obj = CommonUtils.getParam(key, context);

                if (obj == null) {
                    logger.info("{} {} = null", LOG_PREFIX, String.format(PARAM_FORMAT, key));
                    continue;
                }

                formatAndLogValue(key, obj);
            }
        } catch (Exception e) {
            logger.error("{} Error: {}", LOG_PREFIX, e.getMessage(), e);
        }
    }

    private void formatAndLogValue(String key, Object obj) {
        try {
            String formattedValue = formatValueByType(obj);
            logger.info("{} {} = {} ",
                LOG_PREFIX,
                String.format(PARAM_FORMAT, key),
                formattedValue);
        } catch (Exception e) {
            logger.warn("{} Error formatting [{}]: {}", LOG_PREFIX, key, e.getMessage());
        }
    }

    private String formatValueByType(Object obj) {
        if (obj instanceof String) {
            return String.valueOf(obj);
        } else if (obj instanceof Integer) {
            return String.valueOf(obj);
        } else if (obj instanceof Float || obj instanceof Double) {
            return String.format("%.2f", Double.parseDouble(obj.toString()));
        } else if (obj instanceof Boolean) {
            return String.valueOf(obj);
        } else if (obj instanceof Date) {
            return formatDate(obj);
        } else if (obj instanceof Long) {
            return String.valueOf(obj);
        } else if (obj instanceof Object[]) {
            return formatArray(obj);
        } else if (obj instanceof List) {
            return formatArray(obj);
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        } else {
            return formatObject(obj);
        }
    }

    private String formatDate(Object date) {
        if (date instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        return date.toString();
    }

    private String formatArray(Object array) {
        if (array instanceof Object[]) {
            return Arrays.toString((Object[]) array);
        } else if (array instanceof List) {
            return Arrays.toString(((List<?>) array).toArray());
        }
        return array.toString();
    }

    private String formatObject(Object obj) {
        try {
            return new ObjectMapper()
                .writer()
                .writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}