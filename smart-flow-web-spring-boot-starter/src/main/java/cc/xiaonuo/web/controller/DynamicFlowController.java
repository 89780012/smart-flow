package cc.xiaonuo.web.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import cc.xiaonuo.common.exception.FlowException;
import cc.xiaonuo.flow.engine.FlowContext;
import cc.xiaonuo.flow.engine.FlowEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class DynamicFlowController {
    private final FlowEngine flowEngine;
    private final String flowId;
    private final ObjectMapper objectMapper;

    public DynamicFlowController(FlowEngine flowEngine, String flowId) {
        this.flowEngine = flowEngine;
        this.flowId = flowId;
        objectMapper = new ObjectMapper();
        // 配置Java 8日期时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void executeFlow(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> params = new HashMap<>();
            // 1. 处理 URL 参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    params.put(key, values.length == 1 ? values[0] : values);
                }
            }

            // 2. 根据 Content-Type 处理请求体
            String contentType = request.getContentType();
            if (contentType != null) {
                if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    // 处理 application/x-www-form-urlencoded
                    String body = getRequestBody(request);
                    if (StrUtil.isNotBlank(body)) {
                        Map<String, Object> formParams = parseUrlEncodedBody(body);
                        params.putAll(formParams);
                    }
                } else if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    // 处理 application/json
                    String body = getRequestBody(request);
                    if (StrUtil.isNotBlank(body)) {
                        Map<String, Object> jsonParams = objectMapper.readValue(body,
                                new TypeReference<Map<String, Object>>() {});
                        params.putAll(jsonParams);
                    }
                } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    // 处理 multipart/form-data
                    handleMultipartRequest(request, params);
                }
            }
            FlowContext context = new FlowContext(flowId, params,request,response);
            Object result = flowEngine.execute(flowId, context);
            if (result != null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"); // 设置内容类型和编码
                response.getWriter().write(objectMapper.writeValueAsString(result));
            }
        } catch (FlowException e) {
            log.error("流程执行异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("流程执行异常: {}", e.getMessage(), e);
            throw new FlowException("流程执行异常");
        }
    }

    private Map<String, Object> parseUrlEncodedBody(String body) throws IOException {
        Map<String, Object> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                
                // 处理数组参数 (key[] 或 key 重复的情况)
                if (key.endsWith("[]")) {
                    key = key.substring(0, key.length() - 2);
                    @SuppressWarnings("unchecked")
                    List<String> values = (List<String>) params.computeIfAbsent(key, k -> new ArrayList<String>());
                    values.add(value);
                } else {
                    Object existingValue = params.get(key);
                    if (existingValue != null) {
                        // 如果已存在值,转换为数组
                        if (existingValue instanceof List) {
                            ((List<String>) existingValue).add(value);
                        } else {
                            List<String> values = new ArrayList<>();
                            values.add((String) existingValue);
                            values.add(value);
                            params.put(key, values);
                        }
                    } else {
                        params.put(key, value);
                    }
                }
            }
        }
        return params;
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void handleMultipartRequest(HttpServletRequest request, Map<String, Object> params) throws Exception {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            
            // 处理文件参数
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
    }


} 