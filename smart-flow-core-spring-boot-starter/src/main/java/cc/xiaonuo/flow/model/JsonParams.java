package cc.xiaonuo.flow.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class JsonParams implements Serializable {
    @JacksonXmlProperty(localName = "content")
    @JacksonXmlElementWrapper(useWrapping = false)
    private String content;
}
