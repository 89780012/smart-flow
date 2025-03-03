package cc.xiaonuo.flow.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BodyParams implements Serializable {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "bodyParam")
    private List<Param> bodyParam;
}
