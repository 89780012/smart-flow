package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

@Data
public class Connection implements Serializable {
    @JacksonXmlProperty(localName = "id")
    private String id;
    
    @JacksonXmlProperty(localName = "from")
    private String from;
    
    @JacksonXmlProperty(localName = "to")
    private String to;
    
    @JacksonXmlProperty(localName = "startX")
    private int startX;
    
    @JacksonXmlProperty(localName = "startY")
    private int startY;
    
    @JacksonXmlProperty(localName = "endX")
    private int endX;
    
    @JacksonXmlProperty(localName = "endY")
    private int endY;
    
    @JacksonXmlProperty(localName = "controlX")
    private int controlX;
    
    @JacksonXmlProperty(localName = "controlY")
    private int controlY;
    
    @JacksonXmlProperty(localName = "label")
    private String label;
    
    @JacksonXmlProperty(localName = "expression")
    private String expression;
} 