package cc.xiaonuo.flow.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "biz")
public class BizDefinition implements Serializable {
    @JacksonXmlProperty(localName = "id")
    private String id;
    
    @JacksonXmlProperty(localName = "name")
    private String name;
    
    @JacksonXmlProperty(localName = "url")
    private String url;
    
    @JacksonXmlProperty(localName = "protocol")
    private String protocol;
    
    @JacksonXmlProperty(localName = "method")
    private String method;
    
    @JacksonXmlProperty(localName = "params")
    private Params params;
    
    @JacksonXmlProperty(localName = "results")
    private Results results;
    
    @JacksonXmlProperty(localName = "flows")
    private Flows flows;
} 