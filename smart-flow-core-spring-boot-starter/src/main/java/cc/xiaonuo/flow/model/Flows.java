package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

@Data
public class Flows implements Serializable {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "component")
    private List<CusComponent> cusComponent;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "connection")
    private List<Connection> connection;
} 