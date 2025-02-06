package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

@Data
public class Param implements Serializable {
    @JacksonXmlProperty(localName = "name")
    private String name;
    
    @JacksonXmlProperty(localName = "type")
    private String type;
    
    @JacksonXmlProperty(localName = "required")
    private String required;
    
    @JacksonXmlProperty(localName = "defaultValue")
    private String defaultValue;
    
    @JacksonXmlProperty(localName = "description")
    private String description;
} 