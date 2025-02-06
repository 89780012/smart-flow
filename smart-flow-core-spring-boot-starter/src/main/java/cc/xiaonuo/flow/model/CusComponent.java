package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

@Data
public class CusComponent implements Serializable {
    @JacksonXmlProperty(localName = "id")
    private String id;
    
    @JacksonXmlProperty(localName = "type")
    private String type;
    
    @JacksonXmlProperty(localName = "name")
    private String name;
    
    @JacksonXmlProperty(localName = "x")
    private int x;
    
    @JacksonXmlProperty(localName = "y")
    private int y;
    
    @JacksonXmlProperty(localName = "property")
    private Property property;
} 