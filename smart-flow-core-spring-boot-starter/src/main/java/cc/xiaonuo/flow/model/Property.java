package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

@Data
public class Property implements Serializable {
    @JacksonXmlProperty(localName = "beanRef")
    private String beanRef;
    
    @JacksonXmlProperty(localName = "method")
    private String method;
    
    @JacksonXmlProperty(localName = "threadType")
    private String threadType;

    /**
     * 表格参数
     */
    @JacksonXmlElementWrapper(localName = "params")
    @JacksonXmlProperty(localName = "param")
    private List<PropertyParam> params;

    @JacksonXmlProperty(localName = "sql")
    private String sql;

    @JacksonXmlProperty(localName = "script")
    private String script;

    @JacksonXmlProperty(localName = "returnType")
    private String returnType;

    @JacksonXmlProperty(localName = "dataSourceKey")
    private String dataSourceKey;

    @JacksonXmlProperty(localName = "bindKey")
    private String bindKey;

    @JacksonXmlProperty(localName = "paginationType")
    private String paginationType;

    @JacksonXmlProperty(localName = "operationType")
    private String operationType;
}