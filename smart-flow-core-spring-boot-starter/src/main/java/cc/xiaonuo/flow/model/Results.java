package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

@Data
public class Results implements Serializable {
    @JacksonXmlProperty(localName = "responseStruct")
    private String responseStruct;
    
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "result")
    private List<ResultItem> result;
}