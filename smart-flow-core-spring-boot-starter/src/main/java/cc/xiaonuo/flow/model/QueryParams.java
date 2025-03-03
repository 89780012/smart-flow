package cc.xiaonuo.flow.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryParams implements Serializable {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "queryParam")
    private List<Param> queryParam;
}